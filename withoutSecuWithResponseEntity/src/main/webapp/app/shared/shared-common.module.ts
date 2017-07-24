import { NgModule } from '@angular/core';
import { Title } from '@angular/platform-browser';

import {
    DefaultMongoDbSharedLibsModule,
    JhiAlertComponent,
    JhiAlertErrorComponent
} from './';

@NgModule({
    imports: [
        DefaultMongoDbSharedLibsModule
    ],
    declarations: [
        JhiAlertComponent,
        JhiAlertErrorComponent
    ],
    providers: [
        Title
    ],
    exports: [
        DefaultMongoDbSharedLibsModule,
        JhiAlertComponent,
        JhiAlertErrorComponent
    ]
})
export class DefaultMongoDbSharedCommonModule {}
