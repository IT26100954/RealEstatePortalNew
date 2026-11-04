# Complete production Java class inventory
Source: 4aef4b56657c13fee5f19befe995195a378abe3a. 76 top-level Java types.
Fields, constructors and declared method signatures are extracted from this source snapshot.
Accessor methods are retained here. Java record accessors and inherited JpaRepository methods are generated/inherited, not listed as hand-written declarations.
Nested named mapper classes and anonymous implementations are described separately under their enclosing type.

## Application

### PortalApplication (class)
Package: com.realestate. Source: `backend/src/main/java/com/realestate/PortalApplication.java`.
- public: `main(String[] args): void`.

## Common helpers

### CsrfTokens (class)
Package: com.realestate.common. Source: `backend/src/main/java/com/realestate/common/CsrfTokens.java`.
- Field: `ATTRIBUTE: String`.
- Constructor: `CsrfTokens()`.
- public: `getOrCreate(HttpSession session): String`.

### CurrentAccount (class)
Package: com.realestate.common. Source: `backend/src/main/java/com/realestate/common/CurrentAccount.java`.
- Field: `id: String`.
- Field: `role: String`.
- Constructor: `CurrentAccount(String id, String role)`.
- public: `getId(): String`.
- public: `getRole(): String`.
- public: `isBuyer(): boolean`.
- public: `isSeller(): boolean`.
- public: `isAdmin(): boolean`.

### CurrentUser (class)
Package: com.realestate.common. Source: `backend/src/main/java/com/realestate/common/CurrentUser.java`.
- Field: `SESSION_USER_ID: String`.
- Field: `jdbc: JdbcTemplate`.
- Constructor: `CurrentUser(JdbcTemplate jdbc)`.
- public: `optional(HttpSession session): CurrentAccount`.
- public: `required(HttpSession session): CurrentAccount`.
- Anonymous RowMapper<CurrentAccount> implements mapRow(ResultSet,int).

### InputRules (class)
Package: com.realestate.common. Source: `backend/src/main/java/com/realestate/common/InputRules.java`.
- Constructor: `InputRules()`.
- public: `invalid(String message): ModuleException`.
- public: `allow(boolean allowed): void`.
- public: `conflict(boolean condition, String message): void`.
- public: `text(String value, String field, int max): String`.
- public: `optional(String value, int max): String`.
- public: `id(String value): String`.
- public: `choice(String value, String... choices): String`.
- public: `email(String value): String`.
- public: `money(String value): BigDecimal`.
- Nested named types: ID, ID.

### ModuleCsrfConfig (class)
Package: com.realestate.common. Source: `backend/src/main/java/com/realestate/common/ModuleCsrfConfig.java`.
- public: `addInterceptors(InterceptorRegistry registry): void`.

### ModuleErrors (class)
Package: com.realestate.common. Source: `backend/src/main/java/com/realestate/common/ModuleErrors.java`.
- public: `expected(ModuleException e): ResponseEntity<Map<String, String>>`.
- public: `invalid(Exception e): ResponseEntity<Map<String, String>>`.
- public: `conflict(DataIntegrityViolationException e): ResponseEntity<Map<String, String>>`.
- public: `concurrent(ConcurrencyFailureException e): ResponseEntity<Map<String, String>>`.
- private: `response(HttpStatus status, String code, String message): ResponseEntity<Map<String, String>>`.
- Nested named types: or.

### ModuleException (class)
Package: com.realestate.common. Source: `backend/src/main/java/com/realestate/common/ModuleException.java`.
- Field: `status: HttpStatus`.
- Field: `code: String`.
- Constructor: `ModuleException(HttpStatus status, String code, String message)`.
- public: `getStatus(): HttpStatus`.
- public: `getCode(): String`.

### PropertyLookup (class)
Package: com.realestate.common. Source: `backend/src/main/java/com/realestate/common/PropertyLookup.java`.
- Field: `jdbc: JdbcTemplate`.
- Constructor: `PropertyLookup(JdbcTemplate jdbc)`.
- public: `find(String id): PropertyReference`.
- public: `lock(String id): PropertyReference`.
- private: `query(String id, boolean locked): PropertyReference`.

### PropertyReference (class)
Package: com.realestate.common. Source: `backend/src/main/java/com/realestate/common/PropertyReference.java`.
- Field: `id: String`.
- Field: `ownerId: String`.
- Field: `status: String`.
- Constructor: `PropertyReference(String id, String ownerId, String status)`.
- public: `getId(): String`.
- public: `getOwnerId(): String`.
- public: `getStatus(): String`.

## User and Access Management

### AuthController (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/AuthController.java`.
- Field: `users: UserService`.
- Field: `auth: AuthService`.
- Field: `currentUser: CurrentUser`.
- Constructor: `AuthController(UserService users,AuthService auth,CurrentUser currentUser)`.
- public: `me(HttpServletRequest request,HttpServletResponse response): AuthResponse`.
- public: `register(@RequestBody CreateUserRequest request): UserResponse`.
- public: `login(@RequestBody LoginRequest request,HttpServletRequest httpRequest,HttpServletResponse response): AuthResponse`.
- public: `logout(HttpServletRequest request): void`.

### AuthService (interface)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/AuthService.java`.
- public: `login(LoginRequest request,HttpServletRequest httpRequest): AuthResponse`.

### AuthServiceImpl (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/AuthServiceImpl.java`.
- Field: `repository: UserRepository`.
- Field: `encoder: PasswordEncoder`.
- Field: `sessions: UserSessions`.
- Field: `dummyHash: String`.
- Constructor: `AuthServiceImpl(UserRepository repository,PasswordEncoder encoder,UserSessions sessions)`.
- public: `login(LoginRequest request,HttpServletRequest httpRequest): AuthResponse`.
- private: `failed(): ModuleException`.

### BootstrapAdminRunner (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/BootstrapAdminRunner.java`.
- Field: `service: BootstrapAdminService`.
- Field: `environment: Environment`.
- Constructor: `BootstrapAdminRunner(BootstrapAdminService service,Environment environment)`.
- public: `run(ApplicationArguments args): void`.

### BootstrapAdminService (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/BootstrapAdminService.java`.
- Field: `repository: UserRepository`.
- Field: `encoder: PasswordEncoder`.
- Constructor: `BootstrapAdminService(UserRepository repository,PasswordEncoder encoder)`.
- public: `createFirstAdmin(String username,String password,String email,String phone): void`.

### AuthResponse (class)
Package: com.realestate.user.dto. Source: `backend/src/main/java/com/realestate/user/dto/AuthResponse.java`.
- Field: `user: UserResponse`.
- Field: `csrfToken: String`.
- Constructor: `AuthResponse(UserResponse user,String csrfToken)`.
- public: `getUser(): UserResponse`.
- public: `getCsrfToken(): String`.

### ChangePasswordRequest (class)
Package: com.realestate.user.dto. Source: `backend/src/main/java/com/realestate/user/dto/ChangePasswordRequest.java`.
- Field: `currentPassword: String`.
- Field: `newPassword: String`.
- Constructor: `ChangePasswordRequest()`.
- public: `getCurrentPassword(): String`.
- public: `setCurrentPassword(String currentPassword): void`.
- public: `getNewPassword(): String`.
- public: `setNewPassword(String newPassword): void`.
- public: `rejectUnknown(String field, Object value): void`.

### ChangeRoleRequest (class)
Package: com.realestate.user.dto. Source: `backend/src/main/java/com/realestate/user/dto/ChangeRoleRequest.java`.
- Field: `role: Role`.
- Constructor: `ChangeRoleRequest()`.
- public: `getRole(): Role`.
- public: `setRole(Role role): void`.
- public: `rejectUnknown(String field, Object value): void`.

### CreateUserRequest (class)
Package: com.realestate.user.dto. Source: `backend/src/main/java/com/realestate/user/dto/CreateUserRequest.java`.
- Field: `username: String`.
- Field: `password: String`.
- Field: `email: String`.
- Field: `phone: String`.
- Field: `role: Role`.
- Constructor: `CreateUserRequest()`.
- public: `getUsername(): String`.
- public: `setUsername(String username): void`.
- public: `getPassword(): String`.
- public: `setPassword(String password): void`.
- public: `getEmail(): String`.
- public: `setEmail(String email): void`.
- public: `getPhone(): String`.
- public: `setPhone(String phone): void`.
- public: `getRole(): Role`.
- public: `setRole(Role role): void`.
- public: `rejectUnknown(String field, Object value): void`.

### LoginRequest (class)
Package: com.realestate.user.dto. Source: `backend/src/main/java/com/realestate/user/dto/LoginRequest.java`.
- Field: `username: String`.
- Field: `password: String`.
- Constructor: `LoginRequest()`.
- public: `getUsername(): String`.
- public: `setUsername(String username): void`.
- public: `getPassword(): String`.
- public: `setPassword(String password): void`.
- public: `rejectUnknown(String field, Object value): void`.

### UpdateUserRequest (class)
Package: com.realestate.user.dto. Source: `backend/src/main/java/com/realestate/user/dto/UpdateUserRequest.java`.
- Field: `username: String`.
- Field: `email: String`.
- Field: `phone: String`.
- Constructor: `UpdateUserRequest()`.
- public: `getUsername(): String`.
- public: `setUsername(String username): void`.
- public: `getEmail(): String`.
- public: `setEmail(String email): void`.
- public: `getPhone(): String`.
- public: `setPhone(String phone): void`.
- public: `rejectUnknown(String field, Object value): void`.

### UserResponse (class)
Package: com.realestate.user.dto. Source: `backend/src/main/java/com/realestate/user/dto/UserResponse.java`.
- Field: `id: String`.
- Field: `username: String`.
- Field: `email: String`.
- Field: `phone: String`.
- Field: `role: Role`.
- Constructor: `UserResponse()`.
- public: `getId(): String`.
- public: `setId(String id): void`.
- public: `getUsername(): String`.
- public: `setUsername(String username): void`.
- public: `getEmail(): String`.
- public: `setEmail(String email): void`.
- public: `getPhone(): String`.
- public: `setPhone(String phone): void`.
- public: `getRole(): Role`.
- public: `setRole(Role role): void`.

### PasswordConfig (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/PasswordConfig.java`.
- public: `passwordEncoder(): PasswordEncoder`.

### Role (enum)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/Role.java`.
Values: BUYER, SELLER, ADMIN.

### User (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/User.java`.
- Field: `id: String`.
- Field: `username: String`.
- Field: `passwordHash: String`.
- Field: `email: String`.
- Field: `phone: String`.
- Field: `role: Role`.
- Constructor: `User()`.
- public: `getId(): String`.
- public: `setId(String id): void`.
- public: `getUsername(): String`.
- public: `setUsername(String username): void`.
- public: `getPasswordHash(): String`.
- public: `setPasswordHash(String passwordHash): void`.
- public: `getEmail(): String`.
- public: `setEmail(String email): void`.
- public: `getPhone(): String`.
- public: `setPhone(String phone): void`.
- public: `getRole(): Role`.
- public: `setRole(Role role): void`.

### UserApiExceptionHandler (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/UserApiExceptionHandler.java`.
- public: `expected(ModuleException e): ResponseEntity<Map<String,String>>`.
- public: `invalid(HttpMessageNotReadableException e): ResponseEntity<Map<String,String>>`.
- public: `conflict(DataIntegrityViolationException e): ResponseEntity<Map<String,String>>`.
- public: `concurrent(ConcurrencyFailureException e): ResponseEntity<Map<String,String>>`.
- private: `response(HttpStatus status,String code,String message): ResponseEntity<Map<String,String>>`.

### UserController (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/UserController.java`.
- Field: `service: UserService`.
- Field: `currentUser: CurrentUser`.
- Constructor: `UserController(UserService service,CurrentUser currentUser)`.
- public: `list(HttpSession session): List<UserResponse>`.
- public: `detail(@PathVariable String id,HttpSession session): UserResponse`.
- public: `create(@RequestBody CreateUserRequest request,HttpSession session): UserResponse`.
- public: `update(@PathVariable String id,@RequestBody UpdateUserRequest request,HttpSession session): UserResponse`.
- public: `role(@PathVariable String id,@RequestBody ChangeRoleRequest request,HttpSession session): UserResponse`.
- public: `password(@PathVariable String id,@RequestBody ChangePasswordRequest request,HttpSession session): void`.
- public: `delete(@PathVariable String id,HttpSession session): void`.

### UserRepository (interface)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/UserRepository.java`.
- public: `findByUsernameIgnoreCase(String username): Optional<User>`.
- public: `existsByUsernameIgnoreCase(String username): boolean`.
- public: `countByRole(Role role): long`.
- public: `lockAllUsers(): List<User>`.
- Inherits JpaRepository<User,String>; Spring Data supplies CRUD implementation at runtime.

### UserService (interface)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/UserService.java`.
- public: `register(CreateUserRequest request): UserResponse`.
- public: `createUser(String currentUserId, CreateUserRequest request): UserResponse`.
- public: `getAllUsers(String currentUserId): List<UserResponse>`.
- public: `getUserById(String currentUserId, String userId): UserResponse`.
- public: `updateUser(String currentUserId, String userId, UpdateUserRequest request): UserResponse`.
- public: `changePassword(String currentUserId, String userId, ChangePasswordRequest request): void`.
- public: `changeRole(String currentUserId, String userId, ChangeRoleRequest request): UserResponse`.
- public: `deleteUser(String currentUserId, String userId): void`.

### UserServiceImpl (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/UserServiceImpl.java`.
- Field: `repository: UserRepository`.
- Field: `encoder: PasswordEncoder`.
- Field: `jdbc: JdbcTemplate`.
- Field: `sessions: UserSessions`.
- Constructor: `UserServiceImpl(UserRepository repository,PasswordEncoder encoder,JdbcTemplate jdbc,UserSessions sessions)`.
- private: `missing(): ModuleException`.
- private: `find(String userId): User`.
- private: `findIn(List<User> users,String userId): User`.
- private: `actor(String userId): User`.
- private: `actorIn(List<User> users,String userId): User`.
- private: `admin(User user): void`.
- private: `unique(String name,String excludedId): void`.
- private: `password(String value): String`.
- private: `newUser(CreateUserRequest request): User`.
- private: `references(String userId): int`.
- private: `admins(List<User> users): long`.
- public: `register(CreateUserRequest request): UserResponse`.
- public: `createUser(String currentUserId,CreateUserRequest request): UserResponse`.
- public: `getAllUsers(String currentUserId): List<UserResponse>`.
- public: `getUserById(String currentUserId,String userId): UserResponse`.
- public: `updateUser(String currentUserId,String userId,UpdateUserRequest request): UserResponse`.
- public: `changePassword(String currentUserId,String userId,ChangePasswordRequest request): void`.
- public: `changeRole(String currentUserId,String userId,ChangeRoleRequest request): UserResponse`.
- public: `deleteUser(String currentUserId,String userId): void`.

### UserSessionListenerConfig (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/UserSessionListenerConfig.java`.
- public: `userSessionListener(final UserSessions sessions): ServletListenerRegistrationBean<HttpSessionListener>`.
- Anonymous HttpSessionListener implements sessionDestroyed(HttpSessionEvent).

### UserSessions (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/UserSessions.java`.
- Field: `sessions: Map<String,Set<HttpSession>>`.
- public: `track(String userId,HttpSession session): void`.
- public: `remove(HttpSession session): void`.
- public: `invalidate(String userId): void`.
- public: `invalidateAfterCommit(final String userId): void`.
- Anonymous transaction synchronization defers invalidation until commit.

### UserViews (class)
Package: com.realestate.user. Source: `backend/src/main/java/com/realestate/user/UserViews.java`.
- Constructor: `UserViews()`.
- public: `response(User user): UserResponse`.

## Property Listing Management

### CreatePropertyRequest (class)
Package: com.realestate.property.dto. Source: `backend/src/main/java/com/realestate/property/dto/CreatePropertyRequest.java`.
- Field: `title: String`.
- Field: `description: String`.
- Field: `city: String`.
- Field: `address: String`.
- Field: `price: String`.
- Field: `propertyType: String`.
- Field: `status: String`.
- Constructor: `CreatePropertyRequest()`.
- public: `getTitle(): String`.
- public: `setTitle(String title): void`.
- public: `getDescription(): String`.
- public: `setDescription(String description): void`.
- public: `getCity(): String`.
- public: `setCity(String city): void`.
- public: `getAddress(): String`.
- public: `setAddress(String address): void`.
- public: `getPrice(): String`.
- public: `setPrice(String price): void`.
- public: `getPropertyType(): String`.
- public: `setPropertyType(String propertyType): void`.
- public: `getStatus(): String`.
- public: `setStatus(String status): void`.
- public: `rejectUnknown(String field, Object value): void`.

### PropertyResponse (class)
Package: com.realestate.property.dto. Source: `backend/src/main/java/com/realestate/property/dto/PropertyResponse.java`.
- Field: `id: String`.
- Field: `ownerId: String`.
- Field: `title: String`.
- Field: `description: String`.
- Field: `city: String`.
- Field: `address: String`.
- Field: `price: String`.
- Field: `propertyType: String`.
- Field: `status: String`.
- Constructor: `PropertyResponse()`.
- public: `getId(): String`.
- public: `setId(String id): void`.
- public: `getOwnerId(): String`.
- public: `setOwnerId(String ownerId): void`.
- public: `getTitle(): String`.
- public: `setTitle(String title): void`.
- public: `getDescription(): String`.
- public: `setDescription(String description): void`.
- public: `getCity(): String`.
- public: `setCity(String city): void`.
- public: `getAddress(): String`.
- public: `setAddress(String address): void`.
- public: `getPrice(): String`.
- public: `setPrice(String price): void`.
- public: `getPropertyType(): String`.
- public: `setPropertyType(String propertyType): void`.
- public: `getStatus(): String`.
- public: `setStatus(String status): void`.

### UpdatePropertyRequest (class)
Package: com.realestate.property.dto. Source: `backend/src/main/java/com/realestate/property/dto/UpdatePropertyRequest.java`.
- Field: `title: String`.
- Field: `description: String`.
- Field: `city: String`.
- Field: `address: String`.
- Field: `price: String`.
- Field: `propertyType: String`.
- Field: `status: String`.
- Constructor: `UpdatePropertyRequest()`.
- public: `getTitle(): String`.
- public: `setTitle(String title): void`.
- public: `getDescription(): String`.
- public: `setDescription(String description): void`.
- public: `getCity(): String`.
- public: `setCity(String city): void`.
- public: `getAddress(): String`.
- public: `setAddress(String address): void`.
- public: `getPrice(): String`.
- public: `setPrice(String price): void`.
- public: `getPropertyType(): String`.
- public: `setPropertyType(String propertyType): void`.
- public: `getStatus(): String`.
- public: `setStatus(String status): void`.
- public: `rejectUnknown(String field, Object value): void`.

### Property (class)
Package: com.realestate.property. Source: `backend/src/main/java/com/realestate/property/Property.java`.
- Field: `id: String`.
- Field: `ownerId: String`.
- Field: `title: String`.
- Field: `description: String`.
- Field: `city: String`.
- Field: `address: String`.
- Field: `price: BigDecimal`.
- Field: `propertyType: String`.
- Field: `status: String`.
- Constructor: `Property()`.
- public: `getId(): String`.
- public: `setId(String id): void`.
- public: `getOwnerId(): String`.
- public: `setOwnerId(String ownerId): void`.
- public: `getTitle(): String`.
- public: `setTitle(String title): void`.
- public: `getDescription(): String`.
- public: `setDescription(String description): void`.
- public: `getCity(): String`.
- public: `setCity(String city): void`.
- public: `getAddress(): String`.
- public: `setAddress(String address): void`.
- public: `getPrice(): BigDecimal`.
- public: `setPrice(BigDecimal price): void`.
- public: `getPropertyType(): String`.
- public: `setPropertyType(String propertyType): void`.
- public: `getStatus(): String`.
- public: `setStatus(String status): void`.

### PropertyController (class)
Package: com.realestate.property. Source: `backend/src/main/java/com/realestate/property/PropertyController.java`.
- Field: `service: PropertyService`.
- Field: `currentUser: CurrentUser`.
- Constructor: `PropertyController(PropertyService service, CurrentUser currentUser)`.
- public: `detail(@PathVariable String id, HttpSession session): PropertyResponse`.
- public: `create(@RequestBody CreatePropertyRequest request, HttpSession session): PropertyResponse`.
- public: `update(@PathVariable String id,@RequestBody UpdatePropertyRequest request,HttpSession session): PropertyResponse`.
- public: `delete(@PathVariable String id,HttpSession session): void`.

### PropertyRepository (interface)
Package: com.realestate.property. Source: `backend/src/main/java/com/realestate/property/PropertyRepository.java`.
- public: `findAll(): List<Property>`.
- public: `findById(String id): Property`.
- public: `lockById(String id): Property`.
- public: `insert(Property record): void`.
- public: `update(Property record): void`.
- public: `delete(String id): void`.

### PropertyRepositoryImpl (class)
Package: com.realestate.property. Source: `backend/src/main/java/com/realestate/property/PropertyRepositoryImpl.java`.
- Field: `jdbc: JdbcTemplate`.
- Constructor: `PropertyRepositoryImpl(JdbcTemplate jdbc)`.
- public: `findAll(): List<Property>`.
- public: `findById(String id): Property`.
- public: `lockById(String id): Property`.
- private: `find(String id, boolean locked): Property`.
- public: `insert(Property record): void`.
- public: `update(Property record): void`.
- public: `delete(String id): void`.
- Nested named types: Mapper.

### PropertyService (interface)
Package: com.realestate.property. Source: `backend/src/main/java/com/realestate/property/PropertyService.java`.
- public: `create(CreatePropertyRequest request, CurrentAccount actor): PropertyResponse`.
- public: `detail(String id, CurrentAccount actor): PropertyResponse`.
- public: `list(CurrentAccount actor, String q, String city): List<PropertyResponse>`.
- public: `update(String id, UpdatePropertyRequest request, CurrentAccount actor): PropertyResponse`.
- public: `delete(String id, CurrentAccount actor): void`.

### PropertyServiceImpl (class)
Package: com.realestate.property. Source: `backend/src/main/java/com/realestate/property/PropertyServiceImpl.java`.
- Field: `repository: PropertyRepository`.
- Field: `properties: PropertyLookup`.
- Constructor: `PropertyServiceImpl(PropertyRepository repository, PropertyLookup properties)`.
- private: `owns(String ownerId, CurrentAccount actor): boolean`.
- private: `response(Property record): PropertyResponse`.
- private: `readable(Property record, CurrentAccount actor): boolean`.
- public: `detail(String recordId, CurrentAccount actor): PropertyResponse`.
- public: `list(CurrentAccount actor, String q, String city): List<PropertyResponse>`.
- public: `create(CreatePropertyRequest request, CurrentAccount actor): PropertyResponse`.
- private: `fill(Property record, CreatePropertyRequest request): void`.
- private: `fill(Property record, UpdatePropertyRequest request): void`.
- public: `update(String recordId, UpdatePropertyRequest request, CurrentAccount actor): PropertyResponse`.
- public: `delete(String recordId, CurrentAccount actor): void`.

## Moving Service Directory Management

### MovingCompany (record)
Package: com.realestate.moving. Source: `backend/src/main/java/com/realestate/moving/MovingCompany.java`.
- Field: `id: String`.
- Field: `companyName: String`.
- Field: `email: String`.
- Field: `phone: String`.
- Field: `serviceAreas: String`.
- Field: `description: String`.
- Field: `availability: String`.
- Record-generated component accessors, equals, hashCode and toString are supplied by Java.

### MovingCompanyController (class)
Package: com.realestate.moving. Source: `backend/src/main/java/com/realestate/moving/MovingCompanyController.java`.
- Field: `service: MovingCompanyService`.
- Field: `currentUser: CurrentUser`.
- Constructor: `MovingCompanyController(MovingCompanyService service,CurrentUser currentUser)`.
- public: `list(HttpSession session): List<MovingCompany>`.
- public: `detail(@PathVariable String id,HttpSession session): MovingCompany`.
- public: `create(@RequestBody MovingCompanyRequest request,HttpSession session): MovingCompany`.
- public: `update(@PathVariable String id,@RequestBody MovingCompanyRequest request,HttpSession session): MovingCompany`.
- public: `delete(@PathVariable String id,HttpSession session): void`.

### MovingCompanyDAO (class)
Package: com.realestate.moving. Source: `backend/src/main/java/com/realestate/moving/MovingCompanyDAO.java`.
- Field: `jdbc: JdbcTemplate`.
- Constructor: `MovingCompanyDAO(JdbcTemplate jdbc)`.
- public: `all(): List<MovingCompany>`.
- public: `find(String id): MovingCompany`.
- public: `lock(String id): MovingCompany`.
- private: `read(String id,boolean locked): MovingCompany`.
- private: `missing(): ModuleException`.
- public: `insert(MovingCompany record): void`.
- public: `update(MovingCompany record): void`.
- public: `delete(String id): void`.
- Nested named types: CompanyMapper.

### MovingCompanyRequest (class)
Package: com.realestate.moving. Source: `backend/src/main/java/com/realestate/moving/MovingCompanyRequest.java`.
- Field: `companyName: String`.
- Field: `email: String`.
- Field: `phone: String`.
- Field: `serviceAreas: String`.
- Field: `description: String`.
- Field: `availability: String`.
- Constructor: `MovingCompanyRequest()`.
- public: `getCompanyName(): String`.
- public: `setCompanyName(String companyName): void`.
- public: `getEmail(): String`.
- public: `setEmail(String email): void`.
- public: `getPhone(): String`.
- public: `setPhone(String phone): void`.
- public: `getServiceAreas(): String`.
- public: `setServiceAreas(String serviceAreas): void`.
- public: `getDescription(): String`.
- public: `setDescription(String description): void`.
- public: `getAvailability(): String`.
- public: `setAvailability(String availability): void`.
- public: `rejectUnknown(String name,Object value): void`.

### MovingCompanyService (class)
Package: com.realestate.moving. Source: `backend/src/main/java/com/realestate/moving/MovingCompanyService.java`.
- Field: `dao: MovingCompanyDAO`.
- Constructor: `MovingCompanyService(MovingCompanyDAO dao)`.
- public: `list(CurrentAccount actor): List<MovingCompany>`.
- public: `detail(String recordId,CurrentAccount actor): MovingCompany`.
- private: `requireAdmin(CurrentAccount actor): void`.
- private: `prepare(String id,MovingCompanyRequest request): MovingCompany`.
- public: `create(MovingCompanyRequest request,CurrentAccount actor): MovingCompany`.
- public: `update(String recordId,MovingCompanyRequest request,CurrentAccount actor): MovingCompany`.
- public: `delete(String recordId,CurrentAccount actor): void`.

## Viewing Appointment Management

### Appointment (class)
Package: com.realestate.appointment. Source: `backend/src/main/java/com/realestate/appointment/Appointment.java`.
- Field: `id: String`.
- Field: `buyerId: String`.
- Field: `propertyId: String`.
- Field: `viewingDate: LocalDate`.
- Field: `viewingTime: LocalTime`.
- Field: `status: String`.
- Field: `notes: String`.
- Constructor: `Appointment()`.
- public: `getId(): String`.
- public: `setId(String id): void`.
- public: `getBuyerId(): String`.
- public: `setBuyerId(String buyerId): void`.
- public: `getPropertyId(): String`.
- public: `setPropertyId(String propertyId): void`.
- public: `getViewingDate(): LocalDate`.
- public: `setViewingDate(LocalDate viewingDate): void`.
- public: `getViewingTime(): LocalTime`.
- public: `setViewingTime(LocalTime viewingTime): void`.
- public: `getStatus(): String`.
- public: `setStatus(String status): void`.
- public: `getNotes(): String`.
- public: `setNotes(String notes): void`.

### AppointmentController (class)
Package: com.realestate.appointment. Source: `backend/src/main/java/com/realestate/appointment/AppointmentController.java`.
- Field: `service: AppointmentService`.
- Field: `currentUser: CurrentUser`.
- Constructor: `AppointmentController(AppointmentService service, CurrentUser currentUser)`.
- public: `detail(@PathVariable String id, HttpSession session): AppointmentResponse`.
- public: `create(@RequestBody CreateAppointmentRequest request, HttpSession session): AppointmentResponse`.
- public: `update(@PathVariable String id,@RequestBody UpdateAppointmentRequest request,HttpSession session): AppointmentResponse`.
- public: `delete(@PathVariable String id,HttpSession session): void`.
- public: `status(@PathVariable String id,@RequestBody AppointmentStatusRequest request,HttpSession session): AppointmentResponse`.

### AppointmentRepository (interface)
Package: com.realestate.appointment. Source: `backend/src/main/java/com/realestate/appointment/AppointmentRepository.java`.
- public: `findAll(): List<Appointment>`.
- public: `findById(String id): Appointment`.
- public: `lockById(String id): Appointment`.
- public: `insert(Appointment record): void`.
- public: `update(Appointment record): void`.
- public: `delete(String id): void`.
- public: `countConfirmedSlot(String propertyId, java.time.LocalDate date, java.time.LocalTime time, String excludedId): int`.

### AppointmentRepositoryImpl (class)
Package: com.realestate.appointment. Source: `backend/src/main/java/com/realestate/appointment/AppointmentRepositoryImpl.java`.
- Field: `jdbc: JdbcTemplate`.
- Constructor: `AppointmentRepositoryImpl(JdbcTemplate jdbc)`.
- public: `findAll(): List<Appointment>`.
- public: `findById(String id): Appointment`.
- public: `lockById(String id): Appointment`.
- private: `find(String id, boolean locked): Appointment`.
- public: `insert(Appointment record): void`.
- public: `update(Appointment record): void`.
- public: `delete(String id): void`.
- public: `countConfirmedSlot(String propertyId, java.time.LocalDate date, java.time.LocalTime time, String excludedId): int`.
- Nested named types: Mapper.

### AppointmentService (interface)
Package: com.realestate.appointment. Source: `backend/src/main/java/com/realestate/appointment/AppointmentService.java`.
- public: `create(CreateAppointmentRequest request, CurrentAccount actor): AppointmentResponse`.
- public: `detail(String id, CurrentAccount actor): AppointmentResponse`.
- public: `list(CurrentAccount actor, String q, String city): List<AppointmentResponse>`.
- public: `update(String id, UpdateAppointmentRequest request, CurrentAccount actor): AppointmentResponse`.
- public: `delete(String id, CurrentAccount actor): void`.
- public: `changeStatus(String id, String status, CurrentAccount actor): AppointmentResponse`.

### AppointmentServiceImpl (class)
Package: com.realestate.appointment. Source: `backend/src/main/java/com/realestate/appointment/AppointmentServiceImpl.java`.
- Field: `repository: AppointmentRepository`.
- Field: `properties: PropertyLookup`.
- Constructor: `AppointmentServiceImpl(AppointmentRepository repository, PropertyLookup properties)`.
- private: `owns(String ownerId, CurrentAccount actor): boolean`.
- private: `response(Appointment record): AppointmentResponse`.
- private: `readable(Appointment record, CurrentAccount actor): boolean`.
- public: `detail(String recordId, CurrentAccount actor): AppointmentResponse`.
- public: `list(CurrentAccount actor, String q, String city): List<AppointmentResponse>`.
- public: `create(CreateAppointmentRequest request, CurrentAccount actor): AppointmentResponse`.
- private: `fill(Appointment record, CreateAppointmentRequest request): void`.
- private: `fill(Appointment record, UpdateAppointmentRequest request): void`.
- public: `update(String recordId, UpdateAppointmentRequest request, CurrentAccount actor): AppointmentResponse`.
- public: `delete(String recordId, CurrentAccount actor): void`.
- public: `changeStatus(String recordId, String requested, CurrentAccount actor): AppointmentResponse`.

### AppointmentResponse (class)
Package: com.realestate.appointment.dto. Source: `backend/src/main/java/com/realestate/appointment/dto/AppointmentResponse.java`.
- Field: `id: String`.
- Field: `buyerId: String`.
- Field: `propertyId: String`.
- Field: `viewingDate: LocalDate`.
- Field: `viewingTime: LocalTime`.
- Field: `status: String`.
- Field: `notes: String`.
- Constructor: `AppointmentResponse()`.
- public: `getId(): String`.
- public: `setId(String id): void`.
- public: `getBuyerId(): String`.
- public: `setBuyerId(String buyerId): void`.
- public: `getPropertyId(): String`.
- public: `setPropertyId(String propertyId): void`.
- public: `getViewingDate(): LocalDate`.
- public: `setViewingDate(LocalDate viewingDate): void`.
- public: `getViewingTime(): LocalTime`.
- public: `setViewingTime(LocalTime viewingTime): void`.
- public: `getStatus(): String`.
- public: `setStatus(String status): void`.
- public: `getNotes(): String`.
- public: `setNotes(String notes): void`.

### AppointmentStatusRequest (class)
Package: com.realestate.appointment.dto. Source: `backend/src/main/java/com/realestate/appointment/dto/AppointmentStatusRequest.java`.
- Field: `status: String`.
- Constructor: `AppointmentStatusRequest()`.
- public: `getStatus(): String`.
- public: `setStatus(String status): void`.
- public: `rejectUnknown(String field, Object value): void`.

### CreateAppointmentRequest (class)
Package: com.realestate.appointment.dto. Source: `backend/src/main/java/com/realestate/appointment/dto/CreateAppointmentRequest.java`.
- Field: `propertyId: String`.
- Field: `viewingDate: LocalDate`.
- Field: `viewingTime: LocalTime`.
- Field: `notes: String`.
- Constructor: `CreateAppointmentRequest()`.
- public: `getPropertyId(): String`.
- public: `setPropertyId(String propertyId): void`.
- public: `getViewingDate(): LocalDate`.
- public: `setViewingDate(LocalDate viewingDate): void`.
- public: `getViewingTime(): LocalTime`.
- public: `setViewingTime(LocalTime viewingTime): void`.
- public: `getNotes(): String`.
- public: `setNotes(String notes): void`.
- public: `rejectUnknown(String field, Object value): void`.

### UpdateAppointmentRequest (class)
Package: com.realestate.appointment.dto. Source: `backend/src/main/java/com/realestate/appointment/dto/UpdateAppointmentRequest.java`.
- Field: `viewingDate: LocalDate`.
- Field: `viewingTime: LocalTime`.
- Field: `notes: String`.
- Constructor: `UpdateAppointmentRequest()`.
- public: `getViewingDate(): LocalDate`.
- public: `setViewingDate(LocalDate viewingDate): void`.
- public: `getViewingTime(): LocalTime`.
- public: `setViewingTime(LocalTime viewingTime): void`.
- public: `getNotes(): String`.
- public: `setNotes(String notes): void`.
- public: `rejectUnknown(String field, Object value): void`.

## Offer Management

### CreateOfferRequest (class)
Package: com.realestate.offer.dto. Source: `backend/src/main/java/com/realestate/offer/dto/CreateOfferRequest.java`.
- Field: `propertyId: String`.
- Field: `amount: String`.
- Field: `message: String`.
- Constructor: `CreateOfferRequest()`.
- public: `getPropertyId(): String`.
- public: `setPropertyId(String propertyId): void`.
- public: `getAmount(): String`.
- public: `setAmount(String amount): void`.
- public: `getMessage(): String`.
- public: `setMessage(String message): void`.
- public: `rejectUnknown(String field, Object value): void`.

### OfferResponse (class)
Package: com.realestate.offer.dto. Source: `backend/src/main/java/com/realestate/offer/dto/OfferResponse.java`.
- Field: `id: String`.
- Field: `buyerId: String`.
- Field: `propertyId: String`.
- Field: `amount: String`.
- Field: `message: String`.
- Field: `status: String`.
- Constructor: `OfferResponse()`.
- public: `getId(): String`.
- public: `setId(String id): void`.
- public: `getBuyerId(): String`.
- public: `setBuyerId(String buyerId): void`.
- public: `getPropertyId(): String`.
- public: `setPropertyId(String propertyId): void`.
- public: `getAmount(): String`.
- public: `setAmount(String amount): void`.
- public: `getMessage(): String`.
- public: `setMessage(String message): void`.
- public: `getStatus(): String`.
- public: `setStatus(String status): void`.

### OfferStatusRequest (class)
Package: com.realestate.offer.dto. Source: `backend/src/main/java/com/realestate/offer/dto/OfferStatusRequest.java`.
- Field: `status: String`.
- Constructor: `OfferStatusRequest()`.
- public: `getStatus(): String`.
- public: `setStatus(String status): void`.
- public: `rejectUnknown(String field, Object value): void`.

### UpdateOfferRequest (class)
Package: com.realestate.offer.dto. Source: `backend/src/main/java/com/realestate/offer/dto/UpdateOfferRequest.java`.
- Field: `amount: String`.
- Field: `message: String`.
- Constructor: `UpdateOfferRequest()`.
- public: `getAmount(): String`.
- public: `setAmount(String amount): void`.
- public: `getMessage(): String`.
- public: `setMessage(String message): void`.
- public: `rejectUnknown(String field, Object value): void`.

### Offer (class)
Package: com.realestate.offer. Source: `backend/src/main/java/com/realestate/offer/Offer.java`.
- Field: `id: String`.
- Field: `buyerId: String`.
- Field: `propertyId: String`.
- Field: `amount: BigDecimal`.
- Field: `message: String`.
- Field: `status: String`.
- Constructor: `Offer()`.
- public: `getId(): String`.
- public: `setId(String id): void`.
- public: `getBuyerId(): String`.
- public: `setBuyerId(String buyerId): void`.
- public: `getPropertyId(): String`.
- public: `setPropertyId(String propertyId): void`.
- public: `getAmount(): BigDecimal`.
- public: `setAmount(BigDecimal amount): void`.
- public: `getMessage(): String`.
- public: `setMessage(String message): void`.
- public: `getStatus(): String`.
- public: `setStatus(String status): void`.

### OfferController (class)
Package: com.realestate.offer. Source: `backend/src/main/java/com/realestate/offer/OfferController.java`.
- Field: `service: OfferService`.
- Field: `currentUser: CurrentUser`.
- Constructor: `OfferController(OfferService service, CurrentUser currentUser)`.
- public: `detail(@PathVariable String id, HttpSession session): OfferResponse`.
- public: `create(@RequestBody CreateOfferRequest request, HttpSession session): OfferResponse`.
- public: `update(@PathVariable String id,@RequestBody UpdateOfferRequest request,HttpSession session): OfferResponse`.
- public: `delete(@PathVariable String id,HttpSession session): void`.
- public: `status(@PathVariable String id,@RequestBody OfferStatusRequest request,HttpSession session): OfferResponse`.

### OfferRepository (interface)
Package: com.realestate.offer. Source: `backend/src/main/java/com/realestate/offer/OfferRepository.java`.
- public: `findAll(): List<Offer>`.
- public: `findById(String id): Offer`.
- public: `lockById(String id): Offer`.
- public: `insert(Offer record): void`.
- public: `update(Offer record): void`.
- public: `delete(String id): void`.

### OfferRepositoryImpl (class)
Package: com.realestate.offer. Source: `backend/src/main/java/com/realestate/offer/OfferRepositoryImpl.java`.
- Field: `jdbc: JdbcTemplate`.
- Constructor: `OfferRepositoryImpl(JdbcTemplate jdbc)`.
- public: `findAll(): List<Offer>`.
- public: `findById(String id): Offer`.
- public: `lockById(String id): Offer`.
- private: `find(String id, boolean locked): Offer`.
- public: `insert(Offer record): void`.
- public: `update(Offer record): void`.
- public: `delete(String id): void`.
- Nested named types: Mapper.

### OfferService (interface)
Package: com.realestate.offer. Source: `backend/src/main/java/com/realestate/offer/OfferService.java`.
- public: `create(CreateOfferRequest request, CurrentAccount actor): OfferResponse`.
- public: `detail(String id, CurrentAccount actor): OfferResponse`.
- public: `list(CurrentAccount actor, String q, String city): List<OfferResponse>`.
- public: `update(String id, UpdateOfferRequest request, CurrentAccount actor): OfferResponse`.
- public: `delete(String id, CurrentAccount actor): void`.
- public: `changeStatus(String id, String status, CurrentAccount actor): OfferResponse`.

### OfferServiceImpl (class)
Package: com.realestate.offer. Source: `backend/src/main/java/com/realestate/offer/OfferServiceImpl.java`.
- Field: `repository: OfferRepository`.
- Field: `properties: PropertyLookup`.
- Constructor: `OfferServiceImpl(OfferRepository repository, PropertyLookup properties)`.
- private: `owns(String ownerId, CurrentAccount actor): boolean`.
- private: `response(Offer record): OfferResponse`.
- private: `readable(Offer record, CurrentAccount actor): boolean`.
- public: `detail(String recordId, CurrentAccount actor): OfferResponse`.
- public: `list(CurrentAccount actor, String q, String city): List<OfferResponse>`.
- public: `create(CreateOfferRequest request, CurrentAccount actor): OfferResponse`.
- private: `fill(Offer record, CreateOfferRequest request): void`.
- private: `fill(Offer record, UpdateOfferRequest request): void`.
- public: `update(String recordId, UpdateOfferRequest request, CurrentAccount actor): OfferResponse`.
- public: `delete(String recordId, CurrentAccount actor): void`.
- public: `changeStatus(String recordId, String requested, CurrentAccount actor): OfferResponse`.

## Review and Feedback Management

### CreateReviewRequest (class)
Package: com.realestate.review.dto. Source: `backend/src/main/java/com/realestate/review/dto/CreateReviewRequest.java`.
- Field: `propertyId: String`.
- Field: `rating: Integer`.
- Field: `comment: String`.
- Constructor: `CreateReviewRequest()`.
- public: `getPropertyId(): String`.
- public: `setPropertyId(String propertyId): void`.
- public: `getRating(): Integer`.
- public: `setRating(BigDecimal rating): void`.
- public: `getComment(): String`.
- public: `setComment(String comment): void`.
- public: `rejectUnknown(String field, Object value): void`.

### ReviewResponse (class)
Package: com.realestate.review.dto. Source: `backend/src/main/java/com/realestate/review/dto/ReviewResponse.java`.
- Field: `id: String`.
- Field: `reviewerId: String`.
- Field: `propertyId: String`.
- Field: `rating: Integer`.
- Field: `comment: String`.
- Constructor: `ReviewResponse()`.
- public: `getId(): String`.
- public: `setId(String id): void`.
- public: `getReviewerId(): String`.
- public: `setReviewerId(String reviewerId): void`.
- public: `getPropertyId(): String`.
- public: `setPropertyId(String propertyId): void`.
- public: `getRating(): Integer`.
- public: `setRating(Integer rating): void`.
- public: `getComment(): String`.
- public: `setComment(String comment): void`.

### UpdateReviewRequest (class)
Package: com.realestate.review.dto. Source: `backend/src/main/java/com/realestate/review/dto/UpdateReviewRequest.java`.
- Field: `rating: Integer`.
- Field: `comment: String`.
- Constructor: `UpdateReviewRequest()`.
- public: `getRating(): Integer`.
- public: `setRating(BigDecimal rating): void`.
- public: `getComment(): String`.
- public: `setComment(String comment): void`.
- public: `rejectUnknown(String field, Object value): void`.

### Review (class)
Package: com.realestate.review. Source: `backend/src/main/java/com/realestate/review/Review.java`.
- Field: `id: String`.
- Field: `reviewerId: String`.
- Field: `propertyId: String`.
- Field: `rating: Integer`.
- Field: `comment: String`.
- Constructor: `Review()`.
- public: `getId(): String`.
- public: `setId(String id): void`.
- public: `getReviewerId(): String`.
- public: `setReviewerId(String reviewerId): void`.
- public: `getPropertyId(): String`.
- public: `setPropertyId(String propertyId): void`.
- public: `getRating(): Integer`.
- public: `setRating(Integer rating): void`.
- public: `getComment(): String`.
- public: `setComment(String comment): void`.

### ReviewController (class)
Package: com.realestate.review. Source: `backend/src/main/java/com/realestate/review/ReviewController.java`.
- Field: `service: ReviewService`.
- Field: `currentUser: CurrentUser`.
- Constructor: `ReviewController(ReviewService service, CurrentUser currentUser)`.
- public: `detail(@PathVariable String id, HttpSession session): ReviewResponse`.
- public: `create(@RequestBody CreateReviewRequest request, HttpSession session): ReviewResponse`.
- public: `update(@PathVariable String id,@RequestBody UpdateReviewRequest request,HttpSession session): ReviewResponse`.
- public: `delete(@PathVariable String id,HttpSession session): void`.

### ReviewRepository (interface)
Package: com.realestate.review. Source: `backend/src/main/java/com/realestate/review/ReviewRepository.java`.
- public: `findAll(): List<Review>`.
- public: `findById(String id): Review`.
- public: `lockById(String id): Review`.
- public: `insert(Review record): void`.
- public: `update(Review record): void`.
- public: `delete(String id): void`.

### ReviewRepositoryImpl (class)
Package: com.realestate.review. Source: `backend/src/main/java/com/realestate/review/ReviewRepositoryImpl.java`.
- Field: `jdbc: JdbcTemplate`.
- Constructor: `ReviewRepositoryImpl(JdbcTemplate jdbc)`.
- public: `findAll(): List<Review>`.
- public: `findById(String id): Review`.
- public: `lockById(String id): Review`.
- private: `find(String id, boolean locked): Review`.
- public: `insert(Review record): void`.
- public: `update(Review record): void`.
- public: `delete(String id): void`.
- Nested named types: Mapper.

### ReviewService (interface)
Package: com.realestate.review. Source: `backend/src/main/java/com/realestate/review/ReviewService.java`.
- public: `create(CreateReviewRequest request, CurrentAccount actor): ReviewResponse`.
- public: `detail(String id, CurrentAccount actor): ReviewResponse`.
- public: `list(CurrentAccount actor, String q, String city): List<ReviewResponse>`.
- public: `update(String id, UpdateReviewRequest request, CurrentAccount actor): ReviewResponse`.
- public: `delete(String id, CurrentAccount actor): void`.

### ReviewServiceImpl (class)
Package: com.realestate.review. Source: `backend/src/main/java/com/realestate/review/ReviewServiceImpl.java`.
- Field: `repository: ReviewRepository`.
- Field: `properties: PropertyLookup`.
- Constructor: `ReviewServiceImpl(ReviewRepository repository, PropertyLookup properties)`.
- private: `owns(String ownerId, CurrentAccount actor): boolean`.
- private: `response(Review record): ReviewResponse`.
- private: `readable(Review record, CurrentAccount actor): boolean`.
- public: `detail(String recordId, CurrentAccount actor): ReviewResponse`.
- public: `list(CurrentAccount actor, String q, String city): List<ReviewResponse>`.
- public: `create(CreateReviewRequest request, CurrentAccount actor): ReviewResponse`.
- private: `fill(Review record, CreateReviewRequest request): void`.
- private: `fill(Review record, UpdateReviewRequest request): void`.
- public: `update(String recordId, UpdateReviewRequest request, CurrentAccount actor): ReviewResponse`.
- public: `delete(String recordId, CurrentAccount actor): void`.
