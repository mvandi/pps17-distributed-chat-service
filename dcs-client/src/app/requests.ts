export class LoginRequest {

  username: string;

  password: string;

}

export class RegisterRequest {

  username: string;

  firstName: string;

  lastName: string;

  password: string;

  passwordConfirm: string;

}

export class LogoutRequest {

  constructor(public username: string) {
  }

}

export class CreateRoomRequest {

  constructor(public name: string, public username: string) {
  }

}

export class DeleteRoomRequest {

  constructor(public name: string, public username: string) {
  }

}

export class JoinRoomRequest {

  constructor(public username: string) {
  }

}

export class SendMessageRequest {

  constructor(public username: string, public content: string) {
  }

}

export class GetRoomParticipationsRequest {

  constructor(public username: string) {
  }

}
