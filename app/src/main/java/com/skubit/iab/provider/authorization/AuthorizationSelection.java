package com.skubit.iab.provider.authorization;

import com.skubit.iab.provider.base.AbstractSelection;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * Selection for the {@code authorization} table.
 */
public class AuthorizationSelection extends AbstractSelection<AuthorizationSelection> {

    @Override
    protected Uri baseUri() {
        return AuthorizationColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection      A list of which columns to return. Passing null will return all
     *                        columns, which is inefficient.
     * @param sortOrder       How to order the rows, formatted as an SQL ORDER BY clause (excluding
     *                        the ORDER BY itself). Passing null will use the default sort
     *                        order, which may be unordered.
     * @return A {@code AuthorizationCursor} object, which is positioned before the first entry, or
     * null.
     */
    public AuthorizationCursor query(ContentResolver contentResolver, String[] projection,
            String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) {
            return null;
        }
        return new AuthorizationCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null)}.
     */
    public AuthorizationCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null)}.
     */
    public AuthorizationCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public AuthorizationSelection id(long... value) {
        addEquals("authorization." + AuthorizationColumns._ID, toObjectArray(value));
        return this;
    }

    public AuthorizationSelection bitid(String... value) {
        addEquals(AuthorizationColumns.BITID, value);
        return this;
    }

    public AuthorizationSelection bitidNot(String... value) {
        addNotEquals(AuthorizationColumns.BITID, value);
        return this;
    }

    public AuthorizationSelection bitidLike(String... value) {
        addLike(AuthorizationColumns.BITID, value);
        return this;
    }

    public AuthorizationSelection bitidContains(String... value) {
        addContains(AuthorizationColumns.BITID, value);
        return this;
    }

    public AuthorizationSelection bitidStartsWith(String... value) {
        addStartsWith(AuthorizationColumns.BITID, value);
        return this;
    }

    public AuthorizationSelection bitidEndsWith(String... value) {
        addEndsWith(AuthorizationColumns.BITID, value);
        return this;
    }

    public AuthorizationSelection app(String... value) {
        addEquals(AuthorizationColumns.APP, value);
        return this;
    }

    public AuthorizationSelection appNot(String... value) {
        addNotEquals(AuthorizationColumns.APP, value);
        return this;
    }

    public AuthorizationSelection appLike(String... value) {
        addLike(AuthorizationColumns.APP, value);
        return this;
    }

    public AuthorizationSelection appContains(String... value) {
        addContains(AuthorizationColumns.APP, value);
        return this;
    }

    public AuthorizationSelection appStartsWith(String... value) {
        addStartsWith(AuthorizationColumns.APP, value);
        return this;
    }

    public AuthorizationSelection appEndsWith(String... value) {
        addEndsWith(AuthorizationColumns.APP, value);
        return this;
    }

    public AuthorizationSelection scope(String... value) {
        addEquals(AuthorizationColumns.SCOPE, value);
        return this;
    }

    public AuthorizationSelection scopeNot(String... value) {
        addNotEquals(AuthorizationColumns.SCOPE, value);
        return this;
    }

    public AuthorizationSelection scopeLike(String... value) {
        addLike(AuthorizationColumns.SCOPE, value);
        return this;
    }

    public AuthorizationSelection scopeContains(String... value) {
        addContains(AuthorizationColumns.SCOPE, value);
        return this;
    }

    public AuthorizationSelection scopeStartsWith(String... value) {
        addStartsWith(AuthorizationColumns.SCOPE, value);
        return this;
    }

    public AuthorizationSelection scopeEndsWith(String... value) {
        addEndsWith(AuthorizationColumns.SCOPE, value);
        return this;
    }

    public AuthorizationSelection date(Long... value) {
        addEquals(AuthorizationColumns.DATE, value);
        return this;
    }

    public AuthorizationSelection dateNot(Long... value) {
        addNotEquals(AuthorizationColumns.DATE, value);
        return this;
    }

    public AuthorizationSelection dateGt(long value) {
        addGreaterThan(AuthorizationColumns.DATE, value);
        return this;
    }

    public AuthorizationSelection dateGtEq(long value) {
        addGreaterThanOrEquals(AuthorizationColumns.DATE, value);
        return this;
    }

    public AuthorizationSelection dateLt(long value) {
        addLessThan(AuthorizationColumns.DATE, value);
        return this;
    }

    public AuthorizationSelection dateLtEq(long value) {
        addLessThanOrEquals(AuthorizationColumns.DATE, value);
        return this;
    }
}
