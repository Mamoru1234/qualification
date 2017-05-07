package org.challenge.qualification.entities

import org.challenge.qualification.dtos.DelegateDto
import org.challenge.qualification.votes.scanner.Delegate
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*

/**
 */
@Entity
@Table(name = "delegate")
data class DelegateEntity(
        @Id
        @Type(type = "pg-uuid")
        var id: UUID = UUID.randomUUID(),

        var firstName: String = "",
        var middleName: String = "",
        var lastName: String = "",

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "delegate")
        var votes: List<VoteEntity>? = null
)

fun delegateEntityFrom(delegate: Delegate): DelegateEntity {
        return DelegateEntity(
                firstName = delegate.firstName,
                lastName = delegate.lastName,
                middleName = delegate.middleName
        )
}

fun delegateEntityToDto(delegateEntity: DelegateEntity): DelegateDto {
        return DelegateDto(
                id = delegateEntity.id,
                firstName = delegateEntity.firstName,
                middleName = delegateEntity.middleName,
                lastName = delegateEntity.lastName
        )
}

fun findByDelegate(delegate: Delegate): (DelegateEntity) -> Boolean {
        return {
                it.firstName == delegate.firstName
                        && it.middleName == delegate.middleName
                        && it.lastName == delegate.lastName
        }
}
