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
package it.tidalwave.datamanager.application.nogui.args;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.boot.ApplicationArguments;
import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * Some generic utils to manipulate {@link ApplicationArguments}.
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@NoArgsConstructor(access = PRIVATE)
public final class ArgumentsUtils
  {
    /*******************************************************************************************************************
     *
     * Extracts an optional integer option.
     *
     * @param   args    the argument
     * @param   name    the name of the option
     * @return          the value of the option
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Optional<Integer> getIntOption (@Nonnull final ApplicationArguments args, @Nonnull final String name)
      {
        return getStringOption(args, name).map(Integer::parseInt);
      }

    /*******************************************************************************************************************
     *
     * Extracts an optional string option.
     *
     * @param   args    the argument
     * @param   name    the name of the option
     * @return          the value of the option
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Optional<String> getStringOption (@Nonnull final ApplicationArguments args,
                                                    @Nonnull final String name)
      {
        return Optional.ofNullable(args.getOptionValues(name)).flatMap(l -> l.stream().findFirst());
      }

    /*******************************************************************************************************************
     *
     * Find options that are not contained in a given valid collection.
     *
     * @param   args            the application arguments
     * @param   validOptions    the valid options
     * @return                  the bad options
     *
     ******************************************************************************************************************/
    @Nonnull
    public static Set<String> findBadOptions (@Nonnull final ApplicationArguments args,
                                              @Nonnull final Set<String> validOptions)
      {
        final var set = new TreeSet<>(args.getOptionNames());
        set.removeAll(validOptions);
        return set;
      }
  }
