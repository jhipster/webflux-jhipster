import { NgModule } from '@angular/core';
import { Title } from '@angular/platform-browser';

import {
    ReactiveMongoDbSharedLibsModule,
    JhiAlertComponent,
    JhiAlertErrorComponent
} from './';

@NgModule({
    imports: [
        ReactiveMongoDbSharedLibsModule
    ],
    declarations: [
        JhiAlertComponent,
        JhiAlertErrorComponent
    ],
    providers: [
        Title
    ],
    exports: [
        ReactiveMongoDbSharedLibsModule,
        JhiAlertComponent,
        JhiAlertErrorComponent
    ]
})
export class ReactiveMongoDbSharedCommonModule {}
