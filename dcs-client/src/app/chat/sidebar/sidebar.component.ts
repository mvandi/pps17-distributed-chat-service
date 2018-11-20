import {Component, OnInit} from '@angular/core';
import {ChatService} from 'src/app/service/chat.service';
import {filter} from 'rxjs/operators';
import {AuthService} from 'src/app/service/auth.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  query = '';

  search = false;

  showMenu = '';

  constructor(private chat: ChatService, private auth: AuthService) {}

  ngOnInit() {
    this.chat.onRoomJoined()
      .pipe(filter(p => this.search && p.username === this.auth.user.username))
      .subscribe(p => {
        this.query = '';
        this.search = false;
      });
  }

  addExpandClass(element) {
    if (element === this.showMenu) {
      this.showMenu = '0';
    } else {
      this.showMenu = element;
    }
  }

  onFocusout() {
    if (this.query.length === 0)
      this.search = false;
  }

}
