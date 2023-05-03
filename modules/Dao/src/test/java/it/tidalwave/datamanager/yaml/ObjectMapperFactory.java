/***********************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
package it.tidalwave.datamanager.yaml;

import jakarta.annotation.Nonnull;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.tidalwave.util.As;
import it.tidalwave.util.Id;
import it.tidalwave.util.LazySupplier;

/***********************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class ObjectMapperFactory
  {
    @JsonIgnoreType
    static class IgnoreAsMixin {}

    @Nonnull
    public static ObjectMapper getObjectMapper()
      {
        @SuppressWarnings({"unchecked", "rawtypes"})
        final var mapper = new ObjectMapper(new YAMLFactory())
                .addMixIn(As.class, IgnoreAsMixin.class)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.WRAP_ROOT_VALUE)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(new SimpleModule().addSerializer(Id.class, new IdSerializer())
                                                  .addSerializer(LazySupplier.class, new LazySupplierSerializer()));
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                                   .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                                   .withGetterVisibility(JsonAutoDetect.Visibility.NONE));

        return mapper;
      }
  }
