import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CommentBoxModule } from '../comment-box.module';

import { CommentRowComponent } from './comment-row.component';

describe('CommentRowComponent', () => {
  let component: CommentRowComponent;
  let fixture: ComponentFixture<CommentRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CommentRowComponent],
      imports: [CommentBoxModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
