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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import it.tidalwave.util.PreferencesHandler;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class ProductionDataSourceFactoryTest
  {
    @Test
    public void test_database_is_created_from_scratch() // database schema is not tested here
            throws IOException, SQLException
      {
        // given
        final var testAppFolder = Files.createTempDirectory(Path.of("target"), "test-folder-app");
        final var preferencesHandler = mock(PreferencesHandler.class);
        when(preferencesHandler.getAppFolder()).thenReturn(testAppFolder);
        log.info("Test app folder: {}", testAppFolder);
        final var dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setDriverClassName("com.p6spy.engine.spy.P6SpyDriver");
        final var underTest = new ProductionDataSourceFactory();
        // when
        final var dataSource = underTest.getDataSource(dataSourceProperties, preferencesHandler);
        // then
        try (final var conn = dataSource.getConnection();
             final var stat = conn.createStatement())
          {
            stat.execute("SELECT 1");
          }

        final var dbPath = Path.of(dataSourceProperties.getUrl().replaceAll(".*:", ""));
        assertThat(Files.exists(dbPath), is(true));
      }
  }
