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
package it.tidalwave.util.spring.jpa;

import jakarta.annotation.Nonnull;

/***********************************************************************************************************************
 *
 * A mapper that converts JPA entities to model objects.
 *
 * @param   <M> the static type of the model object
 * @param   <E> the static type of the JPA entity
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface EntityModelMapper<M, E>
  {
    /*******************************************************************************************************************
     *
     * Converts a JPA entity to a model object.
     *
     * @param   entity    the entity to convert
     * @return            the model object
     *
     ******************************************************************************************************************/
    @Nonnull
    public M entityToModel (@Nonnull E entity);
  }
