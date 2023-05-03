/***********************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
package it.tidalwave.datamanager.dao.impl.jpa;

import jakarta.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.io.IOException;
import java.nio.file.Path;
import it.tidalwave.util.IdFactory;
import it.tidalwave.datamanager.util.MockIdFactory;
import it.tidalwave.datamanager.util.Utilities;
import it.tidalwave.datamanager.yaml.ObjectMapperFactory;
import lombok.RequiredArgsConstructor;
import static java.util.Comparator.*;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;
import static it.tidalwave.util.StreamUtils.randomLocalDateTimeStream;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public class TestEntityFactory
  {
    private final IdFactory idFactory = new MockIdFactory();

    private final Iterator<LocalDateTime> timestampSequence = randomLocalDateTimeStream(
            17L,
            LocalDateTime.of(2020, 1, 1, 0, 0),
            LocalDateTime.of(2023, 12, 31, 23, 59))
            .iterator();

    /******************************************************************************************************************/
    @Nonnull
    public List<ManagedFileEntity> createManagedFileEntities (final int maxManagedFiles, final int maxFingerprints)
      {
        final var paths = new Random(4)
                .ints(0x2540, 0xffff)
                .limit(maxManagedFiles)
                .mapToObj("/foo/bar/%x"::formatted)
                .toList();
        final var counts = new Random(5).ints(0, maxFingerprints).iterator();
        return paths.stream().map(_f(p -> createManagedFileEntity(p, counts.next()))).toList();
      }

    /******************************************************************************************************************/
    @Nonnull
    public FingerprintEntity createFingerprintEntity (@Nonnull final String fileId)
      {
        return new FingerprintEntity(idFactory.createId().stringValue(),
                                     "name",
                                     "md5",
                                     "fingerprint",
                                     timestampSequence.next(),
                                     fileId);
      }

    /******************************************************************************************************************/
    @Nonnull
    public ManagedFileEntity createManagedFileEntity (@Nonnull final String path, final int fingerprintCount)
      {
        final var entity = new ManagedFileEntity(idFactory.createId().stringValue(), path, List.of());
        final var algorithm = "md5";
        final var bytes = Utilities.fingerprintOfString(algorithm, path);
        final var fingerprint = Utilities.fingerprintToString(bytes);
        final var name = Path.of(path).getFileName().toString();
        final IntFunction<FingerprintEntity> fp = __ -> new FingerprintEntity(idFactory.createId().stringValue(),
                                                                              name,
                                                                              algorithm,
                                                                              fingerprint,
                                                                              timestampSequence.next(),
                                                                              entity.getId());
        entity.setFingerprints(IntStream.range(0, fingerprintCount)
                                        .mapToObj(fp)
                                        .sorted(comparing(FingerprintEntity::getTimestamp))
                                        .toList());
        return entity;
      }

    /******************************************************************************************************************/
    public void dumpToYaml (@Nonnull final Object object, @Nonnull final Path path)
            throws IOException
      {
        ObjectMapperFactory.getObjectMapper().writeValue(path.toFile(), object);
      }
  }
