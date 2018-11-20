export abstract class Toast {
  static toast(message: string) {
    const snackBar = document.getElementById('snackbar');
    snackBar.className = 'show';
    snackBar.innerHTML = message;
    setTimeout(() => snackBar.className = snackBar.className.replace('show', ''), 3000);
  }
}
