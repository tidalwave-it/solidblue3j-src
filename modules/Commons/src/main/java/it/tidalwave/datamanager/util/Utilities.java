package it.tidalwave.datamanager.util;/*
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

import jakarta.annotation.Nonnull;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import static java.nio.charset.StandardCharsets.UTF_8;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class Utilities
  {
    @Nonnull
    public static byte[] fingerprintOfString (@Nonnull final String algorithm, @Nonnull final String string)
      {
        try
          {
            return MessageDigest.getInstance(algorithm).digest(string.getBytes(UTF_8));
          }
        catch (NoSuchAlgorithmException e)
          {
            throw new RuntimeException(e);
          }
      }

    @Nonnull
    public static String fingerprintToString (@Nonnull final byte[] bytes)
      {
        return ("%0" + (bytes.length * 2) + "x").formatted(new BigInteger(1, bytes));
      }
  }
