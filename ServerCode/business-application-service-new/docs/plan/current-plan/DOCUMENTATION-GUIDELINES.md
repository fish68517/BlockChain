# 计划文档编写与并行规划硬性规范（适用于所有新/旧 plan）

> 本规范独立、自洽，不依赖外部文件；任何计划文档（含新增）必须完整遵守，未满足不得合并。

## 适用范围与目标
- 目录：后续新增 plan。
- 对象：service 后端、admin/user 前端、automation-shell/runtime/webui、文档撰写与评审人员。
- 目标：计划文档可直接指导实施，支撑多人/多实例并行，消除模糊空间。

## 必备大纲与每节必填项
1) **背景与目标**：明确业务目的、上线标准。
2) **现状**：精确到文件/类/函数/组件/路由。示例：`service/src/api/v1/cases.py:get_cases`。
3) **缺口**：列点，禁止笼统表述。
4) **设计与对接方案**（必须包含）：
   - API 契约：路径/方法/请求体/响应体示例（JSON 字段名）。
   - 数据表与索引：字段、类型、索引；Alembic 迁移命名 `YYYYMMDDHHMM_TaskID_<slug>`。
   - 模块/组件/包：复用的函数/类型/组件路径，图标名称。
   - ENV/配置：变量名、读取位置、是否必填，需同步 `.env.example` 与 `infra/docker-compose*.yml`。
5) **实施步骤**：按可执行顺序列清单，标注是否可并行/需串行。
6) **测试与验收**（硬性写清）：
   - 测试文件（新增/改）路径。
   - 命令（含 `-k` 过滤）与耗时预估。
   - 前端是否需 E2E；后端 pytest 只能用 `task test:api:run ...`。
7) **风险与依赖**：含外部服务/数据/权限/迁移冲突等。
8) **里程碑**：日期或阶段，含验收标准。
9) **附录**：
   - 必读上下文文件列表。
   - 预计改动行数（总和/主要文件）。
   - 单文件 <500 行约束与拆分方案。
   - 强制静态/类型/测试命令清单。

## 技术与质量硬要求
- **静态检查**：`uv run ruff check src --select F401,F841,F822`（后端）；前端 `pnpm --filter admin lint && pnpm --filter admin type-check`，User/Tauri 用对应 filter。
- **类型检查**：`pnpm type-check`（turbo 触发 service mypy）。
- **测试**：后端一律通过 `task test:api:run ...`；前端涉及页面改动需评估并注明 E2E。
- **ENV 同步**：新增/修改变量必须同时更新 `.env.example`、`infra/docker-compose*.yml`，写明读取位置。
- **RBAC/菜单**：每个后端任务收尾必须 `pnpm run rbac:prepare`，并在文档写明资源名/菜单变更。
- **文件尺寸**：任何单个文件改动后 <500 行，超限需拆分模块/组件。
- **Mock→真实开关**：若使用 mock，必须写明开关变量、默认值、切换时机。

## 并行规划模板（大任务必附）
- 采用 Gate/Wave 结构：Gate（基线）→ Wave-1/2/3。
- 每个 Task 条目必须写：
  - Task ID 与范围
  - 依赖（文件锁/迁移/ENV/RBAC/关键路径）
  - 必读文档与核心代码入口
  - 计划修改/新增的测试文件与命令
  - 预估改动行数（主要文件）与是否需 mock
- **关键路径/文件锁默认优先级**：`core/auth.py` > `core/sse_manager.py` > `api/v1/__init__.py` > Alembic 迁移文件。涉及以上文件必须串行或加锁。
- 并行文档示例可参考 `AUTO-DEV-MASTER-PLAN`（若存在）；缺失时按本模板自建。

## 提交与评审清单（逐条必须满足）
- [ ] 大纲九段齐全，且每段包含必填项。
- [ ] API 契约、表/索引/迁移命名、ENV、RBAC/菜单、组件/图标、测试文件与命令已明确。
- [ ] 单文件 <500 行约束已说明，超限拆分方案已写。
- [ ] 并行规划（Gate/Wave/Task 表）已提供，关键路径与文件锁明确。
- [ ] 同步要求写明：迁移命名、`.env.example` 与 docker-compose 更新、`pnpm run rbac:prepare`。
- [ ] 强制命令列出：ruff、`pnpm type-check`、`task test:api:run ...`、前端 lint/type-check/E2E（如适用）。
- [ ] 无“可选/视情况”模糊措辞。
- [ ] 如引用外部文档，未依赖其存在即可按本规范独立编写。

## 存放与组织规范
- **小任务**：单文档形式，放在 `docs/plan/` 根下（或当前周期子目录），文件命名需含序号或任务标识，遵守本规范大纲与清单。
- **大任务**：必须使用文件夹形式，结构与 `docs/plan/current-plan/` 类似：
  - 在 `docs/plan/<task-name>/` 下建立 `README.md` 作为索引。
  - 若有并行拆分，建立 `tasks/`（或 `task/`）子目录，单独存放 Gate/Wave 拆分的子计划文档，命名含 Task ID。
  - 大任务的 Gate/Wave 规划和 Task 表需写入该文件夹的 README，并同步更新总览（如 `AUTO-DEV-MASTER-PLAN`）保持一致。
- 所有新 plan（小/大）均放在 `docs/plan` 下，不得散落他处；提交前按“提交与评审清单”自检。

## 参考（存在则对齐，不存在也必须按本规范执行）
- `docs/plan/current-plan/AUTO-DEV-MASTER-PLAN.md`
- `docs/plan/current-plan/*` 已就绪的 12 篇计划
- 类型收敛：`05-mypy-phase4-5-closure.md`
- ruff 清理：`12-service-unused-code-cleanup.md`
