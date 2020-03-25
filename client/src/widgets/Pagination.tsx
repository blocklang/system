import { create, tsx } from '@dojo/framework/core/vdom';
import * as c from "bootstrap-classes";

export interface PaginationProperties {
    number: number;
    size: number;
    numberOfElements: number;
    totalElements: number;
    first: boolean;
    last: boolean;
    onPageChanged: (page: number) => void;
}

const factory = create().properties<PaginationProperties>();

export default factory(function Pagination({ properties }){
    const { number, size, numberOfElements, totalElements, first, last, onPageChanged} = properties();
    return (
        <div classes={[c.d_flex, c.justify_content_between]}>
        <div classes={[c.text_muted]}>{`第 ${number * size + 1} 到 ${number * size +numberOfElements} 条，共 ${totalElements} 条记录。`}</div>
        <nav>
            <ul classes={[c.pagination]}>
                <li classes={[c.page_item, first?c.disabled:undefined]}>
                    {
                        first?<a classes={[c.page_link]} href="#" tabindex="-1" aria-disabled="true">上一页</a>:
                        <a href="#" classes={[c.page_link]} onclick={(event: MouseEvent)=>{
                            event.preventDefault();
                            event.stopPropagation();
                            onPageChanged && onPageChanged(number - 1);
                        }}>上一页</a>
                    }
                </li>
                <li classes={[c.page_item, last?c.disabled:undefined]}>
                    {
                        last?<a classes={[c.page_link]} href="#" tabindex="-1" aria-disabled="true">下一页</a>:
                        <a href="#" classes={[c.page_link]} onclick={(event: MouseEvent)=>{
                            event.preventDefault();
                            event.stopPropagation();
                            onPageChanged && onPageChanged(number + 1);
                        }}>下一页</a>
                    }
                </li>
            </ul>
        </nav>
    </div>
    );
});
