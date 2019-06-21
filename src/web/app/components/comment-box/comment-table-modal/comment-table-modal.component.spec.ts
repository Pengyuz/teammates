import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentTableModalComponent } from './comment-table-modal.component';

describe('CommentTableModalComponent', () => {
  let component: CommentTableModalComponent;
  let fixture: ComponentFixture<CommentTableModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CommentTableModalComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentTableModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
