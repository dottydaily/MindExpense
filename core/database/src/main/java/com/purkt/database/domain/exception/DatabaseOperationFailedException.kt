package com.purkt.database.domain.exception

class DatabaseOperationFailedException(operation: String, description: String) :
    Exception("[DB] Operation failed ($operation) : $description")
