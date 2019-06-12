import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from "@angular/forms";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { RichTextEditorModule } from "../rich-text-editor/rich-text-editor.module";
import { CommentBoxComponent } from './comment-box.component';

@NgModule({
  declarations: [CommentBoxComponent],
  imports: [
    CommonModule,
    RichTextEditorModule,
    NgbModule,
    FormsModule,
  ],
  exports: [
      CommentBoxComponent,
  ]
})
export class CommentBoxModule { }
