package com.daqsoft.library_base.config

class ModuleLifecycleReflex {
    companion object{
        // 基础库
        private const val baseInit = "com.daqsoft.library_base.BaseModuleInit"
        // Main
        private const val mainInit = "com.daqsoft.module_main.MainModuleInit"
        // Task
        private const val taskInit = "com.daqsoft.module_task.TaskModuleInit"
        // WorkBench
        private const val workInit = "com.daqsoft.module_work.WorkBenchModuleInit"
        // Statistics
        private const val statisticsInit = "com.daqsoft.module_statistics.StatisticsModuleInit"
        // Mine
        private const val mineInit = "com.daqsoft.module_mine.MineModuleInit"
        // home
        private const val homeInit = "com.daqsoft.module_home.HomeModuleInit"

        val initModuleNames = arrayOf<String>(
            baseInit,mainInit,taskInit,workInit,statisticsInit,mineInit,homeInit
        )
    }
}
