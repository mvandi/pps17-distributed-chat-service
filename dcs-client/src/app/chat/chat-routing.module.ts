import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {ChatComponent} from './chat.component';
import {RoomComponent} from './room/room.component';
import {BlankComponent} from './blank/blank.component';
import {AddRoomComponent} from './add-room/add-room.component';
import {RoomInfoComponent} from './room-info/room-info.component';
import {EditProfileComponent} from "./edit-profile/edit-profile.component";
import {UserProfileComponent} from "./user-profile/user-profile.component";

const routes: Routes = [{
    path: '',
    component: ChatComponent,
    children: [
      {
        path: '',
        component: BlankComponent
      },
      {
        path: 'add-room',
        component: AddRoomComponent
      },
      {
        path: 'rooms/:name',
        component: RoomComponent
      },
      {
        path: 'rooms/:name/info',
        component: RoomInfoComponent
      },
      {
        path: 'users/:username/edit',
        component: EditProfileComponent
      },
      {
        path: 'users/:username/profile',
        component: UserProfileComponent
      }
    ]
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ChatRoutingModule { }
