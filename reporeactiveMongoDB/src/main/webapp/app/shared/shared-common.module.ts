import { NgModule } from '@angular/core';
import { Title } from '@angular/platform-browser';

import {
    ReporeactiveMongoDbSharedLibsModule,
    JhiAlertComponent,
    JhiAlertErrorComponent
} from './';

@NgModule({
    imports: [
        ReporeactiveMongoDbSharedLibsModule
    ],
    declarations: [
        JhiAlertComponent,
        JhiAlertErrorComponent
    ],
    providers: [
        Title
    ],
    exports: [
        ReporeactiveMongoDbSharedLibsModule,
        JhiAlertComponent,
        JhiAlertErrorComponent
    ]
})
export class ReporeactiveMongoDbSharedCommonModule {}
