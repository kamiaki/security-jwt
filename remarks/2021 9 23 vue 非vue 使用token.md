# 2021 9 23 vue	非vue	使用token

插件 js-cookie 安装

```js
import Cookies from 'js-cookie'

const TokenKey = 'autodeploysystem'
const ReportSerialNumber = "sixProject_ReportSerialNumber"
export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}
export function setLoginError(Error) {
  return Cookies.set('login_Errorr', Error)
}
export function getLoginError(Error) {
  return Cookies.get('login_Errorr')
}
export function removeLoginError() {
  return Cookies.remove('login_Errorr')
}
export function setLoginName(userName){
  return Cookies.set('login_userName',userName);
}
export function getLoginName(){
  return Cookies.get('login_userName');
}
export function removeLoginName() {
  return Cookies.remove('login_userName')
}

export function setLoginRole(roleId){
  return Cookies.set('login_roleId',roleId);
}
export function getLoginRole(){
  return Cookies.get('login_roleId');
}
export function removeLoginRole() {
  return Cookies.remove('login_roleId')
}
//存放将要连接的ip
export function setLocalIp(localIp){
  return Cookies.set('localIp',localIp);
}
export function getLocalIp(){
  return Cookies.get('localIp');
}
//存放将要连接的Port
export function getLocalPort(){
  return Cookies.get('localPort');
}
export function setLocalPort(localPort){
  return Cookies.set('localPort',localPort);
}

export function getReportSerialNumber() {
  return Cookies.get(ReportSerialNumber)
}

export function setReportSerialNumber(reportSerialNumber) {
  return Cookies.set(ReportSerialNumber, reportSerialNumber)
}

export function removeReportSerialNumber() {
  return Cookies.remove(ReportSerialNumber)
}
```



vue 实例

```js
// 登录时
//登录接口;
user_login(values.username,md5(values.password+'ctid@1234')).then((response)=>{
    //token放到Session中;token定时刷新，5分钟请求一次;
    if(response.data.success){
        setToken(response.data.token);
        setLoginName(response.data.name);
        setLoginRole(response.data.roleId);
        // window.sessionStorage.setItem("name",response.data.name)
        this.$router.push('/home')
    }else{
        this.$notification.open({
            message: '系统异常',
            description:response.data.message,
            duration: 3,
        });
    }
})

// 发起请求时
request.interceptors.request.use(config => {
  config.headers.token = getToken();
  return config;
});

// 退出登录就没有 后端的事情了 直接失效token 返回登录界面
  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      removeToken() // must remove  token  first
      commit('RESET_STATE')
      resolve()
    })
  },
```



非vue实例 angular

```js
// 登录存入token
//登录按钮
$scope.goLogin = function () {
    var userName = encryption($scope.userName);
    var passwordRef = $scope.password + "guotouyingxin";
    var password = $.md5(passwordRef);
    HttpService.login(userName, password, function (data) {
        if (data.success) {
            $rootScope.loginStatus =true;
            console.log("!!!!!!!!!!!!!!!!!")
            console.log(data)
            localStorageService.set('loginStatus', true);
            localStorageService.set('role', data.roleId);
            localStorageService.set('token', data.token);
            $location.path('/autoMonitor');
            $http.defaults.headers.common['token'] = localStorageService.get('token');
        } else {
            $rootScope.loginStatus = false;
            MessageService.showAlert("登录失败！");
        }
    });
};

// 携带token
table.render({
    elem: '#demo',
    text: {none: '暂无相关数据'}, //默认：无数据。注：该属性为 layui 2.2.5 开始新增
    height: "auto",
    title:'拨测结果统计',
    url: '/network/v1/selectResultByParamHavePaging?startTime='+startTime+'&endTime='+endTime+'&networkType='+networkType+'&code='+code,
    headers:{
        'token':localStorageService.get('token'),
    },
    

    // 登出清除token
    //退出登录;
    $scope.logOut = function () {
    //提示;
    ngDialog.open({
        template: 'dialogs/confirmOperation.html' + urlTimeStamp(),
        className: 'ngdialog-theme-default1',
        width: 600,
        cache: false,
        closeByDocument: false,
        controller: ['$scope', 'HttpService', function ($scope, HttpService) {
            $scope.operationType = "退出登录";
            //点击确定按钮;
            $scope.confirm = function () {
                //清除缓存中的数据;
                $interval.cancel(promise);
                localStorageService.set('token', "");
                $scope.closeThisDialog();
                localStorageService.clearAll();
                $http.defaults.headers.common['token'] = localStorageService.get('token');
                $location.path("/login")
            };
            //点击取消按钮;
            $scope.notSure = function () {
                $scope.closeThisDialog();
            };
        }
                    ]
    });
}
```

