import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RichTextEditorModule } from "../rich-text-editor/rich-text-editor.module";
import { CommentBoxComponent } from './comment-box.component';

@NgModule({
  declarations: [CommentBoxComponent],
  imports: [
    CommonModule,
    RichTextEditorModule,
  ],
  exports: [
      CommentBoxComponent,
  ]
})
export class CommentBoxModule { }
