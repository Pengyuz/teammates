import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CommentBoxComponent } from './comment-box.component';

@NgModule({
  declarations: [CommentBoxComponent],
  imports: [
    CommonModule
  ],
  exports: [
      CommentBoxComponent,
  ]
})
export class CommentBoxModule { }
