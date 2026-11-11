package com.littlehorizons.erp.dao.jdbc;

import java.sql.SQLException;

/**
 * Translates MySQL error codes into clear messages for the user.
 */
public final class SqlErrorTranslator {

    private static final int DUPLICATE_ENTRY = 1062;
    private static final int ROW_IS_REFERENCED = 1451;
    private static final int PARENT_NOT_FOUND = 1452;
    private static final int CHECK_CONSTRAINT_FAILED = 3819;
    private static final int ACCESS_DENIED = 1045;
    private static final int UNKNOWN_DATABASE = 1049;
    private static final String CONNECTION_ERROR_STATE_PREFIX = "08";

    private SqlErrorTranslator() {
    }

    public static String toUserMessage(SQLException exception) {
        String sqlState = exception.getSQLState();
        if (sqlState != null && sqlState.startsWith(CONNECTION_ERROR_STATE_PREFIX)) {
            return "No fue posible conectar con la base de datos. Verifique que MySQL esté en ejecución.";
        }
        switch (exception.getErrorCode()) {
            case DUPLICATE_ENTRY:
                return "Ya existe un registro con esos datos (valor duplicado): " + duplicatedValue(exception);
            case ROW_IS_REFERENCED:
                return "No se puede eliminar o modificar: el registro está siendo usado por otros datos.";
            case PARENT_NOT_FOUND:
                return "El registro relacionado no existe. Verifique los identificadores ingresados.";
            case CHECK_CONSTRAINT_FAILED:
                return "Un valor no cumple las reglas de la base de datos (rango o formato).";
            case ACCESS_DENIED:
                return "Usuario o contraseña de MySQL incorrectos. Revise config.properties.";
            case UNKNOWN_DATABASE:
                return "La base de datos no existe. Ejecute primero docs/database.sql.";
            default:
                return "Ocurrió un error al acceder a la base de datos. Consulte error_log.txt.";
        }
    }

    private static String duplicatedValue(SQLException exception) {
        String message = exception.getMessage();
        int start = message.indexOf('\'');
        int end = message.indexOf('\'', start + 1);
        if (start < 0 || end < 0) {
            return "";
        }
        return message.substring(start + 1, end);
    }
}
