package org.challenge.qualification.extensions

import org.slf4j.Logger

fun Logger.debug(supplier: () -> String) {
    if (this.isDebugEnabled) {
        this.debug(supplier())
    }
}
