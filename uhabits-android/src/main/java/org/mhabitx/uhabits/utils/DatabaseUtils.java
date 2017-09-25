/*
 * Copyright (C) 2016 Alinson Santos Xavier <mhabitx@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.mhabitx.uhabits.utils;

import android.content.*;
import android.database.sqlite.*;
import android.support.annotation.*;

import org.mhabitx.androidbase.utils.*;
import org.mhabitx.uhabits.*;
import org.mhabitx.uhabits.core.*;
import org.mhabitx.uhabits.core.utils.*;

import java.io.*;
import java.text.*;

import static org.mhabitx.uhabits.core.Config.DATABASE_VERSION;

public abstract class DatabaseUtils
{
    @Nullable
    private static HabitsDatabaseOpener opener = null;

    @Deprecated
    public static void executeAsTransaction(Callback callback)
    {
        try (SQLiteDatabase db = openDatabase())
        {
            db.beginTransaction();
            try
            {
                callback.execute();
                db.setTransactionSuccessful();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
                db.endTransaction();
            }
        }
    }

    @NonNull
    public static File getDatabaseFile(Context context)
    {
        String databaseFilename = getDatabaseFilename();
        String root = context.getFilesDir().getPath();

        String format = "%s/../databases/%s";
        String filename = String.format(format, root, databaseFilename);

        return new File(filename);
    }

    @NonNull
    public static String getDatabaseFilename()
    {
        String databaseFilename = Config.DATABASE_FILENAME;
        if (HabitsApplication.Companion.isTestMode()) databaseFilename = "test.db";
        return databaseFilename;
    }

    @SuppressWarnings("unchecked")
    public static void initializeDatabase(Context context)
    {
        opener = new HabitsDatabaseOpener(context, getDatabaseFilename(),
            DATABASE_VERSION);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String saveDatabaseCopy(Context context, File dir)
        throws IOException
    {
        SimpleDateFormat dateFormat = DateFormats.getBackupDateFormat();
        String date = dateFormat.format(DateUtils.getStartOfToday());
        String format = "%s/Loop Habits Backup %s.db";
        String filename = String.format(format, dir.getAbsolutePath(), date);

        File db = getDatabaseFile(context);
        File dbCopy = new File(filename);
        FileUtils.copy(db, dbCopy);

        return dbCopy.getAbsolutePath();
    }

    @NonNull
    public static SQLiteDatabase openDatabase()
    {
        if (opener == null) throw new IllegalStateException();
        return opener.getWritableDatabase();
    }

    public static void dispose()
    {
        opener = null;
    }

    public interface Callback
    {
        void execute() throws Exception;
    }
}
