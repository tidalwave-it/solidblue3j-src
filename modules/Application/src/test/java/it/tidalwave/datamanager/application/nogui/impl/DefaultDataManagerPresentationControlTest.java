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
import it.tidalwave.datamanager.application.nogui.MockBackupFinder;
import it.tidalwave.datamanager.application.nogui.MockDataManagerPresentation;
import it.tidalwave.datamanager.application.nogui.MockManagedFileFinder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static it.tidalwave.datamanager.application.nogui.DataManagerPresentationControl.*;
import static it.tidalwave.datamanager.model.DataManager.BackupFinder.SortingKeys.LABEL;
import static it.tidalwave.datamanager.model.DataManager.ManagedFileFinder.SortingKeys.PATH;
import static it.tidalwave.util.Finder.SortDirection.ASCENDING;
import static it.tidalwave.util.spring.jpa.JpaSpecificationFinder.by;
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

    private Holder<MockManagedFileFinder> managedFileFinder;

    private Holder<MockBackupFinder> backupFinder;

    private final IdFactory idFactory = IdFactory.MOCK;

    /******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        managedFileFinder = Holder.of(h -> new MockManagedFileFinder(h, List.of(
                mockManagedFile("/foo/bar/1","1:f1", "1:f2"),
                mockManagedFile("/foo/bar/2","2:f1", "2:f2", "2:f3"))));
        backupFinder = Holder.of(MockBackupFinder::new);
        dataManager = mock(DataManager.class);
        presentation = new MockDataManagerPresentation();
        when(dataManager.findManagedFiles()).thenReturn(managedFileFinder.f);
        when(dataManager.findBackups()).thenReturn(backupFinder.f);
        underTest = new DefaultDataManagerPresentationControl(dataManager, presentation);
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data()
      {
        // when
        underTest.renderManagedFiles();
        // then
        assertThat(managedFileFinder.f.sorters, is(List.of(Pair.of(by(PATH), ASCENDING))));
        assertThat(presentation.getObjects(), is(List.of("/foo/bar/1", "/foo/bar/2")));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_fingerprints()
      {
        // when
        underTest.renderManagedFiles(ManagedFileOptions.with().renderFingerprints());
        // then
        assertThat(managedFileFinder.f.sorters, is(List.of(Pair.of(by(PATH), ASCENDING))));
        assertThat(presentation.getObjects(), is(List.of("/foo/bar/1", "1:f1", "1:f2",
                                                         "/foo/bar/2", "2:f1", "2:f2", "2:f3")));
        assertThat(managedFileFinder.f.fingerprint, is(Optional.empty()));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_max()
      {
        // when
        underTest.renderManagedFiles(ManagedFileOptions.with().max(1));
        // then
        assertThat(managedFileFinder.f.sorters, is(List.of(Pair.of(by(PATH), ASCENDING))));
        assertThat(presentation.getObjects(), is(List.of("/foo/bar/1")));
        assertThat(managedFileFinder.f.fingerprint, is(Optional.empty()));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_regex()
      {
        // when
        underTest.renderManagedFiles(ManagedFileOptions.with().regex(".*2"));
        // then
        assertThat(managedFileFinder.f.sorters, is(List.of(Pair.of(by(PATH), ASCENDING))));
        assertThat(presentation.getObjects(), is(List.of("/foo/bar/2")));
        assertThat(managedFileFinder.f.fingerprint, is(Optional.empty()));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_fingerprint()
      {
        // when
        underTest.renderManagedFiles(ManagedFileOptions.with().fingerprint("2:f2"));
        // then
        assertThat(managedFileFinder.f.sorters, is(List.of(Pair.of(by(PATH), ASCENDING))));
        // don't assert results, they are not processed by the class under test
        assertThat(managedFileFinder.f.fingerprint, is(Optional.of("2:f2")));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_backups()
      {
        // when
        underTest.renderBackups();
        // then
        assertThat(backupFinder.f.sorters, is(List.of(Pair.of(by(LABEL), ASCENDING))));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_backups_with_files()
      {
        // when
        underTest.renderBackups(BackupOptions.with().renderFiles());
        // then
        assertThat(backupFinder.f.sorters, is(List.of(Pair.of(by(LABEL), ASCENDING))));
        assertThat(backupFinder.f.label, is(Optional.empty()));
        assertThat(backupFinder.f.volumeId, is(Optional.empty()));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_backups_with_label()
      {
        // when
        underTest.renderBackups(BackupOptions.with().label("foo"));
        // then
        assertThat(backupFinder.f.sorters, is(List.of(Pair.of(by(LABEL), ASCENDING))));
        assertThat(backupFinder.f.label, is(Optional.of("foo")));
        assertThat(backupFinder.f.volumeId, is(Optional.empty()));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_backups_with_volumeId()
      {
        // when
        underTest.renderBackups(BackupOptions.with().volumeId("foo"));
        // then
        assertThat(backupFinder.f.sorters, is(List.of(Pair.of(by(LABEL), ASCENDING))));
        assertThat(backupFinder.f.label, is(Optional.empty()));
        assertThat(backupFinder.f.volumeId, is(Optional.of("foo")));
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_backups_with_fileId()
      {
        // when
        underTest.renderBackups(BackupOptions.with().fileId("id"));
        // then
        assertThat(backupFinder.f.sorters, is(List.of(Pair.of(by(LABEL), ASCENDING))));
        assertThat(backupFinder.f.label, is(Optional.empty()));
        assertThat(backupFinder.f.fileId, is(Optional.of("id")));
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
