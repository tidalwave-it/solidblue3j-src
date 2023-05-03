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
package it.tidalwave.datamanager.util;

import jakarta.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;
import it.tidalwave.util.Id;
import it.tidalwave.util.IdFactory;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
// TODO: replace with IdFactory.createMock() when available
public class MockIdFactory implements IdFactory
  {
    private final AtomicInteger sequence = new AtomicInteger(0);

    @Override @Nonnull
    public Id createId()
      {
        return Id.of(String.format("%08x-0000-0000-0000-000000000000", sequence.getAndIncrement()));
      }
  }
