import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CommentEditFormComponent } from '../comment-edit-form.component';
import { CommentRowComponent } from './comment-row.component';

import { CommentTableComponent } from './comment-table.component';

describe('CommentTableComponent', () => {
  let component: CommentTableComponent;
  let fixture: ComponentFixture<CommentTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CommentTableComponent,
        CommentRowComponent,
        CommentEditFormComponent,],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
