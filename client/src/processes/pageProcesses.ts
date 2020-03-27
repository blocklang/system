// 存储页面通用的 processes

import { commandFactory } from "./utils";
import { remove, replace } from '@dojo/framework/stores/state/operations';
import { createProcess } from '@dojo/framework/stores/process';

const clearGlobalTipCommand = commandFactory(({path}) => {
    return [remove(path("globalTip"))];
});

const changeViewCommand = commandFactory<{view: string}>(({path, payload: {view}}) => {
    const result = [];
    // 如果是切换到 new 或 edit，则清除 formValidation
    if(view === "new" || view === "edit") {
        result.push(remove(path("formValidation")));
    }
    result.push(replace(path("pageView"), view))
    return result;
});

export const clearGlobalTipProcess = createProcess("clear-global-tip", [clearGlobalTipCommand]);
export const changeViewProcess = createProcess("change-view", [changeViewCommand]);