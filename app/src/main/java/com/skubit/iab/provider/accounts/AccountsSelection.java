package com.skubit.iab.provider.accounts;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.skubit.iab.provider.base.AbstractSelection;

/**
 * Selection for the {@code accounts} table.
 */
public class AccountsSelection extends AbstractSelection<AccountsSelection> {
    @Override
    protected Uri baseUri() {
        return AccountsColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code AccountsCursor} object, which is positioned before the first entry, or null.
     */
    public AccountsCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new AccountsCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public AccountsCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public AccountsCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code AccountsCursor} object, which is positioned before the first entry, or null.
     */
    public AccountsCursor query(Context context, String[] projection, String sortOrder) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new AccountsCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, projection, null)}.
     */
    public AccountsCursor query(Context context, String[] projection) {
        return query(context, projection, null);
    }

    /**
     * Equivalent of calling {@code query(context, projection, null, null)}.
     */
    public AccountsCursor query(Context context) {
        return query(context, null, null);
    }


    public AccountsSelection id(long... value) {
        addEquals("accounts." + AccountsColumns._ID, toObjectArray(value));
        return this;
    }

    public AccountsSelection bitid(String... value) {
        addEquals(AccountsColumns.BITID, value);
        return this;
    }

    public AccountsSelection bitidNot(String... value) {
        addNotEquals(AccountsColumns.BITID, value);
        return this;
    }

    public AccountsSelection bitidLike(String... value) {
        addLike(AccountsColumns.BITID, value);
        return this;
    }

    public AccountsSelection bitidContains(String... value) {
        addContains(AccountsColumns.BITID, value);
        return this;
    }

    public AccountsSelection bitidStartsWith(String... value) {
        addStartsWith(AccountsColumns.BITID, value);
        return this;
    }

    public AccountsSelection bitidEndsWith(String... value) {
        addEndsWith(AccountsColumns.BITID, value);
        return this;
    }

    public AccountsSelection alias(String... value) {
        addEquals(AccountsColumns.ALIAS, value);
        return this;
    }

    public AccountsSelection aliasNot(String... value) {
        addNotEquals(AccountsColumns.ALIAS, value);
        return this;
    }

    public AccountsSelection aliasLike(String... value) {
        addLike(AccountsColumns.ALIAS, value);
        return this;
    }

    public AccountsSelection aliasContains(String... value) {
        addContains(AccountsColumns.ALIAS, value);
        return this;
    }

    public AccountsSelection aliasStartsWith(String... value) {
        addStartsWith(AccountsColumns.ALIAS, value);
        return this;
    }

    public AccountsSelection aliasEndsWith(String... value) {
        addEndsWith(AccountsColumns.ALIAS, value);
        return this;
    }

    public AccountsSelection authtype(String... value) {
        addEquals(AccountsColumns.AUTHTYPE, value);
        return this;
    }

    public AccountsSelection authtypeNot(String... value) {
        addNotEquals(AccountsColumns.AUTHTYPE, value);
        return this;
    }

    public AccountsSelection authtypeLike(String... value) {
        addLike(AccountsColumns.AUTHTYPE, value);
        return this;
    }

    public AccountsSelection authtypeContains(String... value) {
        addContains(AccountsColumns.AUTHTYPE, value);
        return this;
    }

    public AccountsSelection authtypeStartsWith(String... value) {
        addStartsWith(AccountsColumns.AUTHTYPE, value);
        return this;
    }

    public AccountsSelection authtypeEndsWith(String... value) {
        addEndsWith(AccountsColumns.AUTHTYPE, value);
        return this;
    }

    public AccountsSelection token(String... value) {
        addEquals(AccountsColumns.TOKEN, value);
        return this;
    }

    public AccountsSelection tokenNot(String... value) {
        addNotEquals(AccountsColumns.TOKEN, value);
        return this;
    }

    public AccountsSelection tokenLike(String... value) {
        addLike(AccountsColumns.TOKEN, value);
        return this;
    }

    public AccountsSelection tokenContains(String... value) {
        addContains(AccountsColumns.TOKEN, value);
        return this;
    }

    public AccountsSelection tokenStartsWith(String... value) {
        addStartsWith(AccountsColumns.TOKEN, value);
        return this;
    }

    public AccountsSelection tokenEndsWith(String... value) {
        addEndsWith(AccountsColumns.TOKEN, value);
        return this;
    }

    public AccountsSelection date(Long... value) {
        addEquals(AccountsColumns.DATE, value);
        return this;
    }

    public AccountsSelection dateNot(Long... value) {
        addNotEquals(AccountsColumns.DATE, value);
        return this;
    }

    public AccountsSelection dateGt(long value) {
        addGreaterThan(AccountsColumns.DATE, value);
        return this;
    }

    public AccountsSelection dateGtEq(long value) {
        addGreaterThanOrEquals(AccountsColumns.DATE, value);
        return this;
    }

    public AccountsSelection dateLt(long value) {
        addLessThan(AccountsColumns.DATE, value);
        return this;
    }

    public AccountsSelection dateLtEq(long value) {
        addLessThanOrEquals(AccountsColumns.DATE, value);
        return this;
    }
}
