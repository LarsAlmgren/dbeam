/*-
 * -\-\-
 * DBeam Core
 * --
 * Copyright (C) 2016 - 2018 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.dbeam.options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.Validation;

public interface DBeamPipelineOptions extends PipelineOptions {
  @Description("The JDBC connection url to perform the extraction on.")
  @Validation.Required
  String getConnectionUrl();

  void setConnectionUrl(String value);

  @Description("The database table to query and perform the extraction on.")
  @Validation.Required
  String getTable();

  void setTable(String value);

  @Description("The database user name used by JDBC to authenticate.")
  @Default.String("dbeam-extractor")
  String getUsername();

  void setUsername(String value);

  @Description("A path to a file containing the database password.")
  String getPasswordFile();

  void setPasswordFile(String value);

  @Description("A path to a file containing the database password, "
               + "KMS encrypted and base64 encoded.")
  String getPasswordFileKmsEncrypted();

  void setPasswordFileKmsEncrypted(String value);

  @Description("Database password")
  String getPassword();

  void setPassword(String value);

  @Description("URL to access the vault server.")
  String getVaultUrl();

  void setVaultUrl(String value);

  @Description("Certificate file to use for https when talking to Vault server.")
  String getVaultCert();

  void setVaultCert(String value);

  @Description("Role to use to authenticate to vault.")
  String getVaultRole();

  void setVaultRole(String value);

  @Description("Path in the vault to find username and password values. "
               +  "Example: database/creds/mysql-users-reader")
  String getVaultPath();

  void setVaultPath(String value);
}
