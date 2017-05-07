package org.challenge.qualification.converters

import org.challenge.qualification.votes.scanner.VoteResultType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 */
@Converter(autoApply = true)
class VoteResultTypeConverter: AttributeConverter<VoteResultType, String> {
    override fun convertToDatabaseColumn(attribute: VoteResultType?) = attribute?.toString()

    override fun convertToEntityAttribute(dbData: String?) = dbData
            ?.let { VoteResultType.valueOf(it) }
}