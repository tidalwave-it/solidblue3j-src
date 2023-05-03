/***********************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
package it.tidalwave.datamanager.yaml;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.io.Serial;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import it.tidalwave.util.LazySupplier;

/***********************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
class LazySupplierSerializer<T extends LazySupplier<T>> extends StdSerializer<T>
  {
    @Serial private static final long serialVersionUID = 1L;

    public LazySupplierSerializer()
      {
        this(null);
      }

    public LazySupplierSerializer (final Class<T> t)
      {
        super(t);
      }

    @Override
    public void serialize (@Nonnull final T value,
                           @Nonnull final JsonGenerator jgen,
                           @Nonnull final SerializerProvider provider)
            throws IOException
      {
        if (!value.isInitialized())
          {
            jgen.writeNull();
          }
        else
          {
            jgen.writeObject(value.get());
          }
      }
  }
