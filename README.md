# rookie概述
个人账户系统，包含管理、展示、修改等功能
前端页面设计：页面布局、数据调用及交互逻辑。
安全保密设计：身份认证、权限控制、数据传输及存储安全策略。

# 后端技术
项目构建：Maven
核心框架：Springboot3
MVC框架：Spring MVC
持久层框架：DATA JPA
数据库：MySQL

# 前端技术
Vue3
TypeScript
Vite
Element Plus
Axios
vue-router

# 功能描述
实现了用户认证与授权机制，确保用户身份验证与访问权限控制。
用户信息管理模块提供对用户数据的全面管理，支持权限控制管理模块根据角色和权限进行细粒度的访问控制。
门店信息管理模块处理门店数据的增、删、改、查操作。
机会点管理和任务管理模块分别支持对业务机会点和任务的全生命周期管理。
收藏功能允许用户便捷地保存和访问感兴趣的内容

 配置层 (Config)
 1. application.yml
所在目录:src/main/resources/
功能说明: 项目核心配置文件
主要配置项:
数据库连接配置 (MySQL)
 JPA/Hibernate 配置
JWT安全配置 (密钥、过期时间)
服务器端口配置
CORS跨域配置
2. DataInitializer.java
所在目录:com.laoxiangji.config
功能说明: 数据库初始化组件，系统启动时自动执行
导入包:
 org.springframework.boot.CommandLineRunner
 org.springframework.security.crypto.password.PasswordEncoder
 org.springframework.transaction.annotation.Transactional
主要方法:
 run(): 启动时执行的主方法
initRoles(): 初始化系统角色
initTestUsers(): 创建测试用户账号
3. JwtAuthenticationFilter.java
所在目录:com.laoxiangji.config
功能说明:JWT认证过滤器，处理请求中的JWT令牌

导入包:
 org.springframework.web.filter.OncePerRequestFilter
 org.springframework.security.authentication.UsernamePasswordAuthenticationTok
 en
 jakarta.servlet.FilterChain
主要方法:
 doFilterInternal(): 过滤器核心处理逻辑
parseJwt(): 解析请求头中的JWT令牌
4. SecurityConfig.java
所在目录:com.laoxiangji.config
功能说明:SpringSecurity 安全配置类
导入包:
 org.springframework.security.config.annotation.web.builders.HttpSecurity
 org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 org.springframework.web.cors.CorsConfiguration
主要方法:
 filterChain(): 安全过滤器链配置
authenticationProvider(): 认证提供者配置
corsConfigurationSource(): CORS 配置
3.1.3.4 控制器层 (Controller)
 1. AdminController.java
所在目录:com.laoxiangji.controller
功能说明: 管理员专用控制器，提供管理员级别的用户管理功能
注解:
 @RestController,
 @PreAuthorize("hasRole('ADMIN')")
导入包:
 org.springframework.data.domain.Page
 @RequestMapping("/admin"),
 org.springframework.security.access.prepost.PreAuthorize
 API 端点:
 GET/admin/users: 获取所有用户列表（分页）

2. AuthController.java
所在目录:com.laoxiangji.controller
功能说明: 认证控制器，处理登录、注册、登出、密码重置
注解:@RestController, @RequestMapping("/auth")
导入包:
 jakarta.validation.Valid
 org.springframework.http.ResponseEntity
 API 端点:
 POST/auth/login: 用户登录
POST/auth/register: 用户注册
POST/auth/logout: 用户登出
POST/auth/reset-password: 密码重置
3. FavoriteController.java
所在目录:com.laoxiangji.controller
功能说明: 收藏功能控制器，管理门店和机会点的收藏操作
注 解 : @RestController,
 @PreAuthorize("hasRole('USER')")
 API 端点:
 GET/favorites/stores: 获取收藏的门店列表
GET/favorites/chances: 获取收藏的机会点列表
@RequestMapping("/favorites"),
 PUT/favorites/stores/{id}/toggle: 切换门店收藏状态
PUT/favorites/chances/{id}/toggle: 切换机会点收藏状态
POST/favorites/stores: 创建新门店
PUT/favorites/stores/{id}: 更新门店信息
POST/favorites/chances: 创建新机会点
PUT/favorites/chances/{id}: 更新机会点信息
4. UserController.java
所在目录:com.laoxiangji.controller
功能说明: 用户管理控制器，处理用户信息和任务相关操作
注解:@RestController, @RequestMapping("/users")
 API 端点:
 GET/users/me: 获取当前用户信息及任务

GET/users/{id}: 根据 ID 获取用户详情
PUT/users/update/{id}: 更新用户信息
GET/users/person/{id}: 获取指定用户详情
GET/users/users: 获取所有用户列表
GET/users/managed: 获取可管理的用户列表
PUT/users/tasks/{taskId}: 更新任务进度
服务层 (Service)
 1. AuthService.java
所在目录:com.laoxiangji.service
功能说明: 认证服务类，处理用户认证相关业务逻辑
注解:@Service
导入包:
 org.springframework.security.authentication.AuthenticationManager
 org.springframework.security.crypto.password.PasswordEncoder
 org.springframework.transaction.annotation.Transactional
主要方法:
 authenticateUser(): 用户登录认证
registerUser(): 用户注册
resetPassword(): 密码重置
2. FavoriteService.java
所在目录:com.laoxiangji.service
功能说明: 收藏服务类，处理门店和机会点收藏业务逻辑
注解:@Service
导入包:
 org.springframework.security.core.Authentication
 org.springframework.security.core.context.SecurityContextHolder
主要方法:
 getFavoriteStores(): 获取收藏门店列表
getFavoriteChances(): 获取收藏机会点列表
toggleStoreFavorite(): 切换门店收藏状态
toggleChanceFavorite(): 切换机会点收藏状态

filterByUserLevel(): 根据用户级别过滤数据
createStore(), updateStore(): 门店 CRUD 操作
createChance(), updateChance(): 机会点 CRUD 操作
3. TaskService.java
所在目录:com.laoxiangji.service
功能说明: 任务管理服务类
注解:@Service
主要方法:
 getUserTasks(): 获取用户任务列表
updateTaskProgress(): 更新任务进度
4. UserDetailsServiceImpl.java
所在目录:com.laoxiangji.service
功能说明:SpringSecurity 用户详情服务实现
注解:@Service
实现接口:UserDetailsService
主要方法:
 loadUserByUsername(): 根据用户名加载用户详情
5. UserService.java
所在目录:com.laoxiangji.service
功能说明: 用户管理服务类，处理用户相关业务逻辑
注解:@Service
主要方法:
 getCurrentUserWithTasks(): 获取当前用户及任务信息
updateUser(): 更新用户信息
getManagedUsers(): 获取可管理的用户列表
changePassword(): 修改密码
getUserDetailById(): 根据 ID 获取用户详情
3.1.3.6 仓储层 (Repository)
 1. ChanceRepository.java
所在目录:com.laoxiangji.repository
功能说明: 机会点数据访问接口

继承:JpaRepository<Chance, Long>
自定义查询方法:
 findByFavoriteTrue(): 查找所有收藏的机会点
findByZone(): 按战区查询
findByCity(): 按城市查询
2. RoleRepository.java
所在目录:com.laoxiangji.repository
功能说明: 角色数据访问接口
继承:JpaRepository<Role, Long>
自定义查询方法:
 findByRoleCode(): 根据角色代码查找
existsByRoleCode(): 检查角色代码是否存在
3. StoreRepository.java
所在目录:com.laoxiangji.repository
功能说明: 门店数据访问接口
继承:JpaRepository<Store, Long>
自定义查询方法:
 findByFavoriteTrue(): 查找所有收藏的门店
findByZone(): 按战区查询
findByCity(): 按城市查询
4. TaskRepository.java
所在目录:com.laoxiangji.repository
功能说明: 任务数据访问接口
继承:JpaRepository<Task, Long>
自定义查询方法:
 findByTaskCode(): 根据任务代码查找
5. UserRepository.java
所在目录:com.laoxiangji.repository
功能说明: 用户数据访问接口
继承:JpaRepository<User, Long>
自定义查询方法:
 findByEmployeeId(): 根据员工 ID 查找

findByEmail(): 根据邮箱查找
existsByEmployeeId(): 检查员工 ID 是否存在
existsByEmail(): 检查邮箱是否存在
findByLevelNot(): 查找非指定级别的用户
findByZoneAndLevelIn(): 按战区和级别查询
findByProvinceAndLevel(): 按省份和级别查询
6. UserTaskRepository.java
所在目录:com.laoxiangji.repository
功能说明: 用户任务关联数据访问接口
继承:JpaRepository<UserTask, Long>
自定义查询方法:
 findByUserId(): 根据用户 ID 查找任务
findByUserIdWithTask(): 查找用户任务（包含任务详情）
实体层 (Entity)
 1. Chance.java
所在目录:com.laoxiangji.entity
功能说明: 机会点实体类
注解:@Entity, @Table(name = "t_chance_info")
主要字段:
 id: 主键ID
 chanceCode: 机会点编码
chanceName: 机会点名称
zone, province, city, area: 地理位置信息
address: 详细地址
size, rent, flow: 面积、租金、人流量
competition: 竞争情况
favorite: 收藏状态
2. Role.java
所在目录:com.laoxiangji.entity
功能说明: 角色实体类
注解:@Entity, @Table(name = "t_role")

主要字段:
 id: 主键ID
 roleName: 角色名称
roleCode: 角色代码
description: 角色描述
users: 与用户的多对多关系
3. Store.java
所在目录:com.laoxiangji.entity
功能说明: 门店实体类
注解:@Entity, @Table(name = "t_store_info")
主要字段:
 id: 主键ID
 storeCode: 门店编码
storeName: 门店名称
zone, province, city, area: 地理位置信息
address: 详细地址
opentime: 开业时间
size, seat: 面积、座位数
favorite: 收藏状态
4. Task.java
所在目录:com.laoxiangji.entity
功能说明: 任务实体类
注解:@Entity, @Table(name = "t_task_info")
主要字段:
 id: 主键ID
 taskCode: 任务编码
taskName: 任务名称
taskType: 任务类型
description: 任务描述
status: 任务状态
priority: 优先级
startDate, endDate: 开始和结束时间

5. User.java
所在目录:com.laoxiangji.entity
功能说明: 用户实体类
注解:@Entity, @Table(name = "t_user_info")
主要字段:
 id: 主键ID
 employeeId: 员工 ID
 password: 密码
name: 姓名
zone, province, city: 地理位置信息
level: 用户级别
email, phone: 联系方式
roles: 与角色的多对多关系
6. UserTask.java
所在目录:com.laoxiangji.entity
功能说明: 用户任务关联实体类
注解:@Entity, @Table(name = "t_user_task")
主要字段:
 id: 主键ID
 user: 用户外键
task: 任务外键
assignedDate: 分配时间
completedDate: 完成时间
progress: 进度
notes: 备注
安全层 (Security)
 1. JwtUtils.java
所在目录:com.laoxiangji.security
功能说明:JWT工具类，处理JWT令牌的生成和验证
注解:@Component
导入包:
 io.jsonwebtoken.*
io.jsonwebtoken.security.Keys
主要方法:
 generateJwtToken(): 生成 JWT 令牌
getUserNameFromJwtToken(): 从令牌中提取用户名
validateJwtToken(): 验证 JWT 令牌有效性
2. PermissionUtils.java
所在目录:com.laoxiangji.security
功能说明: 权限工具类，处理复杂的权限判断逻辑
注解:@Component
主要方法:
 canManageUser(): 判断是否可以管理指定用户
getLevelPriority(): 获取用户级别的优先级
canAccessPersonnelManagement(): 判断是否可以访问人员管理
3. UserDetailsImpl.java
所在目录:com.laoxiangji.security
功能说明:SpringSecurity 用户详情实现类
实现接口:UserDetails
导入包:
 org.springframework.security.core.GrantedAuthority
 org.springframework.security.core.userdetails.UserDetails
主要方法:
 build(): 静态工厂方法，构建用户详情对象
getAuthorities(): 获取用户权限列表
 数据传输对象层 (DTO)
 1. ChanceDTO.java
所在目录:com.laoxiangji.dto
功能说明: 机会点数据传输对象
注解:@Data
 2. JwtResponse.java
所在目录:com.laoxiangji.dto
功能说明:JWT认证响应对象

注解:@Data
 3. LoginRequest.java
所在目录:com.laoxiangji.dto
功能说明: 登录请求对象
注解:@Data
验证注解:@NotBlank, @Size
 4. MessageResponse.java
所在目录:com.laoxiangji.dto
功能说明: 通用消息响应对象
注解:@Data
 5. RegisterRequest.java
所在目录:com.laoxiangji.dto
功能说明: 用户注册请求对象
注解:@Data
验证注解:@NotBlank, @Email, @Size
 6. ResetPasswordRequest.java
所在目录:com.laoxiangji.dto
功能说明: 密码重置请求对象
7. StoreDTO.java
所在目录:com.laoxiangji.dto
功能说明: 门店数据传输对象
注解:@Data
 8. TaskDTO.java
所在目录:com.laoxiangji.dto
功能说明: 任务数据传输对象
注解:@Data
 9. TaskProgressRequest.java
所在目录:com.laoxiangji.dto
功能说明: 任务进度更新请求对象
注解:@Data
 10. UserDetailDTO.java
所在目录:com.laoxiangji.dto

功能说明: 用户详细信息传输对象
注解:@Data
 11. UserDTO.java
所在目录:com.laoxiangji.dto
功能说明: 用户基本信息传输对象
注解:@Data
