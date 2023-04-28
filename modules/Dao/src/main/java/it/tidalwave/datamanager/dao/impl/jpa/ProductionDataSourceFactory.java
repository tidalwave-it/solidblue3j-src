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
package it.tidalwave.datamanager.dao.impl.jpa;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import javax.sql.DataSource;
import it.tidalwave.util.PreferencesHandler;

/***********************************************************************************************************************
 *
 * Instantiates a production {@code DataSource} with the proper path for the database.
 *
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Configuration @Profile("production")
public class ProductionDataSourceFactory
  {
    @Bean
    public DataSource getDataSource (@Nonnull final DataSourceProperties properties,
                                     @Nonnull final PreferencesHandler preferencesHandler)
            throws IOException
      {
        final var dbPath = preferencesHandler.getAppFolder().resolve("db/fingerprints.db");
        Files.createDirectories(dbPath.getParent());
        properties.setUrl("jdbc:p6spy:sqlite:" + dbPath.toAbsolutePath());
        return properties.initializeDataSourceBuilder().build();
      }
  }
