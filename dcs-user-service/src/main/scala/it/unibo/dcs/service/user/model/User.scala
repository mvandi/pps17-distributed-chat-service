package it.unibo.dcs.service.user.model

import java.util.Date

final case class User(username: String, firstName: String, lastName: String, bio: String, visible: Boolean, lastSeen: Date)
