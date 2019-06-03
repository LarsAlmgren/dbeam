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

package com.spotify.dbeam.args;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.spotify.dbeam.field.FieldUtils;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePeriod;

/**
 * A POJO describing a how to create a JDBC {@link Connection}.
 */
@AutoValue
public abstract class QueryBuilderArgs implements Serializable {

  public abstract String tableName();

  public abstract Optional<Integer> limit();

  public abstract Optional<String> partitionColumn();

  public abstract Optional<DateTime> partition();

  public abstract ReadablePeriod partitionPeriod();

  public abstract Map<String, Optional<String>> fields();

  public abstract Builder builder();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setTableName(String tableName);

    public abstract Builder setLimit(Integer limit);

    public abstract Builder setLimit(Optional<Integer> limit);

    public abstract Builder setPartitionColumn(String partitionColumn);

    public abstract Builder setPartitionColumn(Optional<String> partitionColumn);

    public abstract Builder setPartition(DateTime partition);

    public abstract Builder setPartition(Optional<DateTime> partition);

    public abstract Builder setPartitionPeriod(ReadablePeriod partitionPeriod);

    public abstract Builder setFields(Map<String, Optional<String>> fields);

    public abstract QueryBuilderArgs build();
  }

  private static Boolean checkTableName(String tableName) {
    return tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
  }

  public static QueryBuilderArgs create(String tableName) {
    Preconditions.checkArgument(tableName != null,
                                "TableName cannot be null");
    Preconditions.checkArgument(checkTableName(tableName),
                                "'table' must follow [a-zA-Z_][a-zA-Z0-9_]*");
    return new AutoValue_QueryBuilderArgs.Builder()
        .setTableName(tableName)
        .setPartitionPeriod(Days.ONE)
        .setFields(new HashMap<>())
        .build();
  }

  public Iterable<String> buildQueries() {
    final String limit = this.limit().map(l -> String.format(" LIMIT %d", l)).orElse("");
    final String where = this.partitionColumn().flatMap(
        partitionColumn ->
            this.partition().map(partition -> {
              final LocalDate datePartition = partition.toLocalDate();
              final String nextPartition = datePartition.plus(partitionPeriod()).toString();
              return String.format(" WHERE %s >= '%s' AND %s < '%s'",
                                   partitionColumn, datePartition, partitionColumn, nextPartition);
            })
    ).orElse("");
    String fieldsList;
    if (fields().isEmpty()) {
      fieldsList = "*";
    } else {
      fieldsList = FieldUtils.createSelectExpression(fields());
    }
    return Lists.newArrayList(
        String.format("SELECT %s FROM %s%s%s", fieldsList, this.tableName(), where, limit));
  }

}
