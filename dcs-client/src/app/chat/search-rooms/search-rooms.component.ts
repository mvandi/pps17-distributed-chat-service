import {Component, Input, OnInit} from '@angular/core';
import {ChatService} from '../../service/chat.service';
import {Router} from '@angular/router';
import {remove} from 'lodash';

import {Room} from '../../model';

@Component({
  selector: 'app-search-rooms',
  templateUrl: './search-rooms.component.html',
  styleUrls: ['./search-rooms.component.scss']
})
export class SearchRoomsComponent implements OnInit {

  @Input()
  query: string;

  rooms: Room[] = [];

  constructor(private chat: ChatService, private router: Router) {}

  ngOnInit() {
    console.log('Searching rooms...');

    this.chat.getRooms()
      .subscribe(rooms => (this.rooms = rooms));

    this.chat.onRoomCreated()
      .subscribe(room => {
        this.rooms.unshift(room);
      });

    this.chat.onRoomDeleted()
      .subscribe(name => remove(this.rooms, room => room.name === name));
  }

  joinRoom(room: Room) {
    console.log(`Joining room: '${room.name}'`);
    this.chat.joinRoom(room.name)
      .subscribe(participation => {
        this.chat.selectRoom(participation.room);
        this.router.navigate(['/rooms', participation.room.name]);
      }, err => console.error(err));
  }

}
