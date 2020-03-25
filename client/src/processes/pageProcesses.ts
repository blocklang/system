// 存储页面通用的 processes

import { commandFactory } from "./utils";
import { remove, replace } from '@dojo/framework/stores/state/operations';
import { createProcess } from '@dojo/framework/stores/process';

const clearGlobalTipCommand = commandFactory(({path}) => {
    return [remove(path("globalTip"))];
});

const changeViewCommand = commandFactory<{view: string}>(({path, payload: {view}}) => {
    return [replace(path("pageView"), view)];
});

export const clearGlobalTipProcess = createProcess("clear-global-tip", [clearGlobalTipCommand]);
export const changeViewProcess = createProcess("change-view", [changeViewCommand]);