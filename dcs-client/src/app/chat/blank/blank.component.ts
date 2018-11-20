import {Component, OnInit} from '@angular/core';

import {AuthService} from '../../service/auth.service';

@Component({
  selector: 'app-blank',
  template: '',
  styleUrls: ['./blank.component.scss']
})
export class BlankComponent implements OnInit {

  constructor(private auth: AuthService) { }

  ngOnInit() {
    console.log(this.auth.user);
  }

}
