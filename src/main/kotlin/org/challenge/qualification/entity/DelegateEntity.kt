package org.challenge.qualification.entity

import org.challenge.qualification.dto.Delegate
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

fun delegateEntityFromDto(delegate: Delegate): DelegateEntity {
        return DelegateEntity(
                firstName = delegate.firstName,
                lastName = delegate.lastName,
                middleName = delegate.middleName
        )
}

fun delegateEntityToDto(delegateEntity: DelegateEntity): Delegate {
        return Delegate(
                id = delegateEntity.id,
                firstName = delegateEntity.firstName,
                middleName = delegateEntity.middleName,
                lastName = delegateEntity.lastName
        )
}
