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

package com.pinewoodbuilders.database.controllers;

import com.pinewoodbuilders.Xeus;
import com.pinewoodbuilders.Constants;
import com.pinewoodbuilders.database.transformers.GlobalSettingsTransformer;
import com.pinewoodbuilders.utilities.CacheUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import java.util.concurrent.TimeUnit;

public class GlobalSettingsController {

    public static final Cache<Long, GlobalSettingsTransformer> cache = CacheBuilder.newBuilder().recordStats()
            .expireAfterAccess(5, TimeUnit.MINUTES).build();

    private static final Logger log = LoggerFactory.getLogger(GlobalSettingsController.class);

    private static final String[] requiredSettingsColumns = new String[] { "global_settings.main_group_name",
            "global_settings.main_group_id", "global_settings.global_ban", "global_settings.global_kick",
            "global_settings.global_verify", "global_settings.global_anti_unban", "global_settings.global_filter",
            "global_settings.global_filter_exact", "global_settings.global_filter_wildcard",
            "global_settings.global_filter_log_channel", "global_settings.global_automod",
            "global_settings.automod_mass_mention", "global_settings.automod_emoji_spam",
            "global_settings.automod_link_spam", "global_settings.automod_message_spam",
            "global_settings.automod_image_spam", "global_settings.automod_character_spam"};

    /**
     * Fetches the guild transformer from the cache, if it doesn't exist in the
     * cache it will be loaded into the cache and then returned afterwords.
     *
     * @param avaire The avaire instance, used to talking to the database.
     * @param guild  The JDA guild instance for the current guild.
     * @return Possibly null, the guild transformer instance for the current guild,
     *         or null.
     */
    @CheckReturnValue
    public static GlobalSettingsTransformer fetchGlobalSettingsFromMGI(Xeus avaire, Long groupId) {
        if (groupId == 0L) {return null;}
        return (GlobalSettingsTransformer) CacheUtil.getUncheckedUnwrapped(cache, groupId,
                () -> loadGuildSettingsFromDatabase(avaire, groupId));
    }

    public static void forgetCache(long groupId) {
        cache.invalidate(groupId);
    }

    private static GlobalSettingsTransformer loadGuildSettingsFromDatabase(Xeus avaire, Long groupId) {
        if (log.isDebugEnabled()) {
            log.debug("Settings cache for " + groupId + " was refreshed");
        }
        try {
            GlobalSettingsTransformer transformer = new GlobalSettingsTransformer(groupId,
                    avaire.getDatabase().newQueryBuilder(Constants.GUILD_SETTINGS_TABLE).select(requiredSettingsColumns)
                            .where("global_settings.main_group_id", groupId).get().first());

            if (!transformer.hasData()) {
                return null;
            }

            return transformer;
        } catch (Exception ex) {
            log.error("Failed to fetch guild transformer from the database, error: {}", ex.getMessage(), ex);

            return null;
        }
    }
}
