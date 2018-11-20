import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {TranslateModule} from '@ngx-translate/core';

import {ChatRoutingModule} from './chat-routing.module';
import {ChatComponent} from './chat.component';
import {MaterialModule} from '../material.module';
import {TopnavComponent} from './topnav/topnav.component';
import {RoomsComponent} from './rooms/rooms.component';
import {RoomComponent} from './room/room.component';
import {BlankComponent} from './blank/blank.component';
import {AddRoomComponent} from './add-room/add-room.component';
import {RoomInfoComponent} from './room-info/room-info.component';
import {FormsModule} from '@angular/forms';
import {SidebarComponent} from './sidebar/sidebar.component';
import {SearchRoomsComponent} from './search-rooms/search-rooms.component';
import {FilterPipe} from './filter.pipe';
import {EditProfileComponent} from './edit-profile/edit-profile.component';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {UserProfileComponent} from './user-profile/user-profile.component';
import {
  MatButtonModule,
  MatCardModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatSidenavModule,
  MatToolbarModule
} from "@angular/material";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ChatRoutingModule,
    MaterialModule,
    TranslateModule,
    MatCheckboxModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatInputModule,
    MatMenuModule,
    MatListModule,
    TranslateModule,
    MatCardModule
  ],
  declarations: [
    ChatComponent,
    TopnavComponent,
    RoomsComponent,
    RoomComponent,
    BlankComponent,
    AddRoomComponent,
    RoomInfoComponent,
    SidebarComponent,
    SearchRoomsComponent,
    FilterPipe,
    EditProfileComponent,
    UserProfileComponent
  ],
})
export class ChatModule {}
