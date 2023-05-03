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
import it.tidalwave.util.Id;

/***********************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
class IdSerializer extends StdSerializer<Id>
  {
    @Serial private static final long serialVersionUID = 1L;

    public IdSerializer ()
      {
        super(Id.class);
      }

    @Override
    public void serialize (@Nonnull final Id value,
                           @Nonnull final JsonGenerator jgen,
                           @Nonnull final SerializerProvider provider)
            throws IOException
      {
        jgen.writeString(value.stringValue());
      }
  }
