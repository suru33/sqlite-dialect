package io.github.suru33.sqlitedialect;


import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.type.StringType;

import java.sql.Types;

/**
 * @author suru33
 */
public class SQLiteDialect extends Dialect {
    public SQLiteDialect() {
        // sqlite3 data types ref: https://www.sqlite.org/draft/datatype3.html
        registerColumnType(Types.TINYINT, "tinyint");
        registerColumnType(Types.SMALLINT, "smallint");
        registerColumnType(Types.INTEGER, "integer");
        registerColumnType(Types.BIGINT, "bigint");

        registerColumnType(Types.CHAR, "character");
        registerColumnType(Types.VARCHAR, "varchar");
        registerColumnType(Types.LONGVARCHAR, "text");
        registerColumnType(Types.CLOB, "text");

        registerColumnType(Types.BINARY, "blob");
        registerColumnType(Types.VARBINARY, "blob");
        registerColumnType(Types.LONGVARBINARY, "blob");
        registerColumnType(Types.BLOB, "blob");

        registerColumnType(Types.REAL, "real");
        registerColumnType(Types.DOUBLE, "double");
        registerColumnType(Types.FLOAT, "float");

        registerColumnType(Types.NUMERIC, "numeric");
        registerColumnType(Types.DECIMAL, "decimal");
        registerColumnType(Types.DATE, "date");
        registerColumnType(Types.TIMESTAMP, "datetime");
        registerColumnType(Types.BOOLEAN, "boolean");

        registerColumnType(Types.NULL, "null");

        registerFunction("concat", new VarArgsSQLFunction(StringType.INSTANCE, "", "||", ""));
        registerFunction("mod", new SQLFunctionTemplate(StringType.INSTANCE, "?1 % ?2"));
        registerFunction("substr", new StandardSQLFunction("substr", StringType.INSTANCE));
        registerFunction("substring", new StandardSQLFunction("substr", StringType.INSTANCE));
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new SQLiteIdentityColumnSupport();
    }

    @Override
    public boolean hasAlterTable() {
        return false;
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public String getDropForeignKeyString() {
        return "";
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable,
                                                   String[] primaryKey, boolean referencesPrimaryKey) {
        return "";
    }

    @Override
    public String getAddPrimaryKeyConstraintString(String constraintName) {
        return "";
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @Override
    public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
        return (sqlException, message, sql) -> {
            int errCode = JdbcExceptionHelper.extractErrorCode(sqlException);
            if (errCode == 19) {
                return new ConstraintViolationException(message, sqlException.getNextException(), "UNIQUE");
            }
            return null;
        };
    }
}
