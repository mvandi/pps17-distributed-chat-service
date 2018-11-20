import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'filter'
})
export class FilterPipe implements PipeTransform {
  transform(items: any[], field: string, query: string): any[] {
    if (!items || !query || query.length === 0) return [];

    query = query.toLowerCase();
    return items.filter(item => {
      return item[field].toLowerCase().includes(query);
    });
  }
}
