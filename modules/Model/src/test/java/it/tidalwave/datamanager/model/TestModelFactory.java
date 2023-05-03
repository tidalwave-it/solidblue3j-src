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
package it.tidalwave.datamanager.model;

import jakarta.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.IntStream;
import java.nio.file.Path;
import it.tidalwave.util.IdFactory;
import it.tidalwave.datamanager.util.MockIdFactory;
import static it.tidalwave.datamanager.util.Utilities.*;
import static it.tidalwave.util.StreamUtils.randomLocalDateTimeStream;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class TestModelFactory
  {
    private final IdFactory idFactory = new MockIdFactory();

    private final Iterator<LocalDateTime> timestampSequence = randomLocalDateTimeStream(
            17L,
            LocalDateTime.of(2020, 1, 1, 0, 0),
            LocalDateTime.of(2023, 12, 31, 23, 59))
            .iterator();

    private final Iterator<Path> pathSequence = new Random(4)
            .ints(0x2540, 0xffff)
            .mapToObj("/foo/bar/%x"::formatted)
            .map(Path::of)
            .iterator();

    /******************************************************************************************************************/
    @Nonnull
    public ManagedFile createManagedFile (final int fingerprintCount)
      {
        return new ManagedFile(idFactory.createId(),
                               pathSequence.next(),
                               () -> IntStream.range(0, fingerprintCount)
                                              .mapToObj(__ -> createFingerprint())
                                              .toList());
      }

    /******************************************************************************************************************/
    @Nonnull
    public Fingerprint createFingerprint()
      {
        final var name = pathSequence.next().getFileName().toString();
        final var algorithm = "md5";
        return Fingerprint.builder()
                          .id(idFactory.createId())
                          .name(name)
                          .algorithm(algorithm)
                          .fingerprint(fingerprintToString(fingerprintOfString(algorithm, name)))
                          .timestamp(timestampSequence.next())
                          .build();
      }
  }
