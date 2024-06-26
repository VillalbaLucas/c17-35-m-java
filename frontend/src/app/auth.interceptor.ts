import { HttpInterceptorFn } from '@angular/common/http'
import { environment } from '../environments/environment'

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const token = localStorage.getItem('token')

    const isApiUrl = req.url.startsWith(environment.apiBaseUrl + '/api')

    if (isApiUrl) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        })
    }

    return next(req)
}
