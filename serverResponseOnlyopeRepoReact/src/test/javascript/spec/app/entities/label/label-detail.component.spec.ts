import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { DefaultMongoDbTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { LabelDetailComponent } from '../../../../../../main/webapp/app/entities/label/label-detail.component';
import { LabelService } from '../../../../../../main/webapp/app/entities/label/label.service';
import { Label } from '../../../../../../main/webapp/app/entities/label/label.model';

describe('Component Tests', () => {

    describe('Label Management Detail Component', () => {
        let comp: LabelDetailComponent;
        let fixture: ComponentFixture<LabelDetailComponent>;
        let service: LabelService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [DefaultMongoDbTestModule],
                declarations: [LabelDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    LabelService,
                    JhiEventManager
                ]
            }).overrideTemplate(LabelDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(LabelDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(LabelService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Label('aaa')));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.label).toEqual(jasmine.objectContaining({id:'aaa'}));
            });
        });
    });

});
