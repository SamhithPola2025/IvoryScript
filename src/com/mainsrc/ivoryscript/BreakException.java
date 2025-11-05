package com.mainsrc.ivoryscript;

class BreakException extends RuntimeException {
    BreakException() {
        super(null, null, false, false);
    }
}