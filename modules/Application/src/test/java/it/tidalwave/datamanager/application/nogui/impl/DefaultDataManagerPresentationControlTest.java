/*
 * *********************************************************************************************************************
 *
 * SolidBlue 3: Data safety
 * http://tidalwave.it/projects/solidblue3
 *
 * Copyright (C) 2023 - 2023 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/solidblue3j-src
 * git clone https://github.com/tidalwave-it/solidblue3j-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.datamanager.application.nogui.impl;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.nio.file.Path;
import it.tidalwave.util.IdFactory;
import it.tidalwave.util.Pair;
import it.tidalwave.datamanager.model.DataManager;
import it.tidalwave.datamanager.model.Fingerprint;
import it.tidalwave.datamanager.model.ManagedFile;
import it.tidalwave.datamanager.application.nogui.Holder;
import it.tidalwave.datamanager.application.nogui.MockDataManagerPresentation;
import it.tidalwave.datamanager.application.nogui.MockManagedFileFinder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static it.tidalwave.util.Finder.SortDirection.ASCENDING;
import static it.tidalwave.util.spring.jpa.JpaRepositoryFinder.by;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SuppressWarnings("FieldCanBeLocal")
public class DefaultDataManagerPresentationControlTest
  {
    private DefaultDataManagerPresentationControl underTest;

    private DataManager dataManager;

    private MockDataManagerPresentation presentation;

    private Holder<MockManagedFileFinder> finder;

    private final IdFactory idFactory = IdFactory.MOCK;

    /******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        finder = Holder.of(h -> new MockManagedFileFinder(h, List.of(
                mockManagedFile("/foo/bar/1","1:f1", "1:f2"),
                mockManagedFile("/foo/bar/2","2:f1", "2:f2", "2:f3"))));
        dataManager = mock(DataManager.class);
        presentation = new MockDataManagerPresentation();
        when(dataManager.findManagedFiles()).thenReturn(finder.f);
        underTest = new DefaultDataManagerPresentationControl(dataManager, presentation);
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data()
      {
        // when
        underTest.renderManagedFiles(false, Optional.empty(), Optional.empty(), Optional.empty(), false);
        // then
        assertThat(finder.f.sorters, is(List.of(Pair.of(by("path"), ASCENDING))));
        assertThat(presentation.getObjects(), is(List.of("/foo/bar/1", "/foo/bar/2")));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_fingerprint()
      {
        // when
        underTest.renderManagedFiles(true, Optional.empty(), Optional.empty(), Optional.empty(), false);
        // then
        assertThat(finder.f.sorters, is(List.of(Pair.of(by("path"), ASCENDING))));
        assertThat(presentation.getObjects(), is(List.of("/foo/bar/1", "1:f1", "1:f2",
                                                         "/foo/bar/2", "2:f1", "2:f2", "2:f3")));
        assertThat(finder.f.fingerprint, is(Optional.empty()));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_max()
      {
        // when
        underTest.renderManagedFiles(false, Optional.of(1), Optional.empty(), Optional.empty(), false);
        // then
        assertThat(finder.f.sorters, is(List.of(Pair.of(by("path"), ASCENDING))));
        assertThat(presentation.getObjects(), is(List.of("/foo/bar/1")));
        assertThat(finder.f.fingerprint, is(Optional.empty()));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_regex()
      {
        // when
        underTest.renderManagedFiles(false, Optional.empty(), Optional.of(".*2"), Optional.empty(), false);
        // then
        assertThat(finder.f.sorters, is(List.of(Pair.of(by("path"), ASCENDING))));
        assertThat(presentation.getObjects(), is(List.of("/foo/bar/2")));
        assertThat(finder.f.fingerprint, is(Optional.empty()));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_fingerprints()
      {
        // when
        underTest.renderManagedFiles(false, Optional.empty(), Optional.empty(), Optional.of("2:f2"), false);
        // then
        assertThat(finder.f.sorters, is(List.of(Pair.of(by("path"), ASCENDING))));
        // don't assert results, they are not processed by the class under test
        assertThat(finder.f.fingerprint, is(Optional.of("2:f2")));
      }

    /******************************************************************************************************************/
    @Nonnull
    private ManagedFile mockManagedFile (@Nonnull final String path, @Nonnull final String ... fingerprints)
      {
        return new ManagedFile(idFactory.createId(), Path.of(path),
                               () -> Stream.of(fingerprints).map(this::mockFingerprint).toList());
      }

    /******************************************************************************************************************/
    @Nonnull
    private Fingerprint mockFingerprint (@Nonnull final String value)
      {
        return Fingerprint.builder().fingerprint(value).id(idFactory.createId()).build();
      }
  }
