import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ChatService} from 'src/app/service/chat.service';
import {filter, throttle} from 'rxjs/operators';
import {fromEvent, interval, Subscription} from "rxjs";
import {AuthService} from "../../service/auth.service";
import {Message} from '../../model';

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.scss']
})
export class RoomComponent implements OnInit {

  name: string;
  message: string = '';
  messages: Message[] = [];

  typingUsers: Array<string> = [];
  private inputSubscription: Subscription;

  constructor(
    private chat: ChatService,
    private route: ActivatedRoute,
    private router: Router,
    private auth: AuthService
  ) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if(this.name !== undefined){
        this.unregisterRealTimeTypingListeners(this.name)
      }
      this.name = params['name'];
      this.registerRealTimeTypingListeners(params['name']);
      this.getMessages(this.name);
    });

    this.chat
      .onRoomDeleted()
      .pipe(filter(name => this.name === name))
      .subscribe(() => this.router.navigateByUrl('/'));

    this.chat
     .onMessageSent()
     .pipe(filter(message => this.name === message.room.name))
     .subscribe(message => this.addMessage(message));
  }

  private registerRealTimeTypingListeners(roomName: string){
    this.registerRoomInputListener(document.querySelector('#message'), roomName);
    this.chat.registerUsersTypingListener(roomName, this.typingUsers)
  }

  private unregisterRealTimeTypingListeners(roomName: string){
    this.inputSubscription.unsubscribe();
    this.chat.unregisterUsersTypingListener(roomName)
  }

  private registerRoomInputListener(inputElement: HTMLInputElement, roomName: string){
    const username = this.auth.user.username;
    const inputSource = fromEvent(inputElement, 'input');
    this.inputSubscription = inputSource.pipe(throttle(() => interval(1000)))
      .subscribe(() => this.chat.notifyTyping(username, roomName))
  }

  private getMessages(roomName: string) {
    console.log('Getting user messages...');
    this.chat
    .getMessagesOnRoom(this.name)
    .subscribe(messages => this.messages = messages);
  }

  sendMessage() {
    console.log("Room name: " + this.name + " Message content: " + this.message);
    this.chat.sendMessage(this.name, this.message)
    .subscribe (
      () => this.message = '',
      err => console.error(err)
    );
  }

  typingUsersWithoutDuplicates(): string {
    return this.typingUsers
      .filter((elem, index, self) => index === self.indexOf(elem))
      .sort((userA, userB) => (userA < userB) ? -1 : (userA > userB) ? 1 : 0)
      .join(', ');
  }

  addMessage (message: Message) {
    console.log(message.username + "sent: " + message.content);
    this.messages.push(message);
  }

  isUserMessage(message: Message): boolean {
    return message.username === this.auth.user.username;
  }


}
