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
import java.util.function.Function;

/***********************************************************************************************************************
 *
 * Mocking classes with fluent-style methods is complex because the mock must be deep; and in any case that approach
 * is heavily limited since we can only verify invocations on the last instance. Here the approach is different: the
 * object with fluent-style methods is referred by a holder, which contains a mutable reference that is always updated
 * with the last instance.
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class Holder<T>
  {
    public T f;

    public static <S> Holder<S> of (@Nonnull final Function<Holder<S>, S> function)
      {
        final var holder = new Holder<S>();
        holder.set(function.apply(holder));
        return holder;
      }

    public void set (@Nonnull final T instance)
      {
        this.f = instance;
      }
  }

