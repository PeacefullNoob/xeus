/*
 * Copyright (c) 2018.
 *
 * This file is part of Xeus.
 *
 * Xeus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Xeus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Xeus.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package com.pinewoodbuilders.database.migrate.migrations;

import com.pinewoodbuilders.Constants;
import com.pinewoodbuilders.contracts.database.migrations.Migration;
import com.pinewoodbuilders.database.connections.MySQL;
import com.pinewoodbuilders.database.schema.Schema;

import java.sql.SQLException;

public class AddMainGroupIdToAntiBanTableMigration implements Migration {

    @Override
    public String created_at() {
        return "Sat, Sep 25, 2021 2:52 PM";
    }

    @Override
    public boolean up(Schema schema) throws SQLException {
        if (schema.hasColumn(Constants.ANTI_UNBAN_TABLE_NAME, "main_group_id")) {
            return true;
        }

        if (schema.getDbm().getConnection() instanceof MySQL) {
            schema.getDbm().queryUpdate(String.format(
                "ALTER TABLE `%s` ADD `main_group_id` BIGINT NULL DEFAULT NULL AFTER `id`;",
                Constants.ANTI_UNBAN_TABLE_NAME
            ));
        } else {
            schema.getDbm().queryUpdate(String.format(
                "ALTER TABLE `%s` ADD `main_group_id` BIGINT;",
                Constants.ANTI_UNBAN_TABLE_NAME
            ));
        }

        return true;
    }

    @Override
    public boolean down(Schema schema) throws SQLException {
        if (!schema.hasColumn(Constants.EVALS_DATABASE_TABLE_NAME, "main_group_id")) {
            return true;
        }

        schema.getDbm().queryUpdate(String.format(
            "ALTER TABLE `%s` DROP `main_group_id`;",
            Constants.EVALS_DATABASE_TABLE_NAME
        ));

        return true;
    }
}
