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
package it.tidalwave.datamanager.application.nogui;

import jakarta.annotation.Nonnull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.slf4j.LoggerFactory;
import it.tidalwave.util.PreferencesHandler;
import it.tidalwave.util.spring.jpa.impl.DefaultFinderJpaRepository;
import it.tidalwave.role.impl.ServiceLoaderLocator;
import it.tidalwave.role.spi.SystemRoleFactory;
import it.tidalwave.role.spring.spi.AnnotationSpringSystemRoleFactory;
import it.tidalwave.datamanager.dao.impl.jpa.JpaDataManagerDao;

/***********************************************************************************************************************
 *
 * The main for the command line (no gui) version of the application.
 *
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SpringBootApplication(scanBasePackages = "it.tidalwave.datamanager")
@ComponentScan({"it.tidalwave.datamanager", "it.tidalwave.util.spring.jpa"})
@EnableJpaRepositories(basePackageClasses = JpaDataManagerDao.class,
                       repositoryBaseClass = DefaultFinderJpaRepository.class)
@EntityScan("it.tidalwave.datamanager.dao")
public class Main
  {
    /*******************************************************************************************************************
     * Primary entry point.
     ******************************************************************************************************************/
    public static void main (@Nonnull final String... args)
      {
        System.setProperty("spring.config.name", "application,module,module-test");
        PreferencesHandler.setAppName("SolidBlue");
        System.setProperty(PreferencesHandler.PROP_SUPPRESS_CONSOLE, "true");
        System.setProperty(ServiceLoaderLocator.PROP_SUPPRESS_CONSOLE, "true");
        System.setProperty("logFolder", PreferencesHandler.getInstance().getLogFolder().toString());
        LoggerFactory.getLogger(Main.class).info("********************************");
        SpringApplication.run(Main.class, args);
      }

    /*******************************************************************************************************************
     * Enables the DCI role annotation scanner.
     ******************************************************************************************************************/
    @Bean
    public AnnotationSpringSystemRoleFactory annotationSpringSystemRoleFactory()
      {
        return (AnnotationSpringSystemRoleFactory)SystemRoleFactory.getInstance();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Bean
    public PreferencesHandler preferencesHandler()
      {
        return PreferencesHandler.getInstance();
      }
  }
